# QueroEmprestar

> **In English, briefly.** A peer-to-peer rental marketplace for physical
> things — a drill, a crutch, a drone, a book. Anyone can be the owner in one
> transaction and the renter in the next, so the whole design hangs on one
> question: *is this item free on those dates?* The answer is not computed in
> Java. It is a `CASE WHEN` inside the `INSERT` itself: if a conflicting booking
> exists, the date column receives `NULL` and the row is rejected by the
> database instead of by the application. That single decision is what this
> README is mostly about — what it buys, and the two gaps it leaves open.
> Java 8, JSF/PrimeFaces, MySQL, no ORM. Written in 2021 as a technical-school
> final project.

Sistema web de **aluguel de objetos entre pessoas**. Um usuário cadastra o que
tem parado em casa — furadeira, muleta, drone, livro — com preço, foto, condição
e endereço; outro procura por nome, categoria ou CEP, pede as datas, e o dono
aceita ou recusa. Quando a locação termina, os dois se avaliam.

O mesmo cadastro é locador e locatário. Não existem dois tipos de conta.

---

## Contexto: por que isto existe

Trabalho de conclusão do curso técnico em informática, em 2021. O enunciado era
construir um sistema web completo com Java + banco relacional; a escolha do
domínio foi minha.

Escolhi aluguel entre pessoas porque é o domínio acadêmico que menos perdoa.
Um CRUD de biblioteca ou de estoque tem uma entidade central e um dono da
verdade. Aqui não: o mesmo usuário é dono de um item e cliente de outro na mesma
semana, os dois lados avaliam um ao outro, e **a agenda de cada objeto é um
recurso disputado** — dois pedidos concorrentes para as mesmas datas é o caso
normal, não a exceção. É onde um sistema mal desenhado quebra.

---

## A decisão técnica mais importante

**A trava de agenda mora dentro do `INSERT`, não no código Java.**

O caminho óbvio seria: consultar as locações do objeto, verificar se a janela
pedida colide, e então inserir. Duas idas ao banco, com uma janela entre elas em
que outro pedido pode entrar. O que está em `dao/LocaçãoDAO.java` (`inserir`) é
outra coisa — a verificação é o próprio valor inserido:

```sql
insert into locacao (dataInicial, dataFinal, preco, ...)
values (
  case when (select count(*) from locacao l
              where l.dataInicial between ? and ?      -- a janela pedida
                and l.idObjeto = ?
                and l.situacao != 'REJEITADO'
                and l.situacao != 'CANCELADO'
                and l.situacao != 'SOLICITADO') = 0
       then ? end,                                     -- senão, NULL

  case when (select count(*) from locacao l
              where l.dataFinal between ? and ?
                and l.idObjeto = ?
                and ... ) = 0
       then ? end,
  ...)
```

Se existe locação conflitante, o `CASE` não tem `ELSE`: a coluna recebe `NULL` e
**o banco recusa a linha**, não a aplicação. Verificação e escrita são uma única
instrução, então não há intervalo entre "conferi" e "gravei" para um segundo
pedido se enfiar.

### O que isso custa

Ser honesto sobre uma decisão inclui dizer onde ela não alcança. Três coisas:

1. **A trava depende de a coluna ser `NOT NULL`, e o schema não está neste
   repositório.** A regra de integridade vive metade no `INSERT` e metade numa
   declaração de coluna que ninguém que clonar o projeto vai ver. Se as colunas
   de data forem nulas, a locação entra com data em branco em vez de ser
   recusada — silenciosamente. É o tipo de acoplamento que uma constraint
   nomeada (`EXCLUDE`/`CHECK`) ou uma transação explícita teriam evitado.

2. **A janela contida não é detectada.** O teste pergunta se alguma reserva
   existente *começa* ou *termina* dentro da janela pedida. Um pedido inteiramente
   dentro de uma locação maior (a existente começa antes e termina depois) não
   satisfaz nenhum dos dois `between` — e passa. O teste correto de sobreposição
   é `novo.inicio <= existente.fim AND novo.fim >= existente.inicio`, que cobre os
   quatro casos com uma comparação só.

3. **`SOLICITADO` não bloqueia, e a aceitação não reconfere.** Pedido pendente
   não segura a agenda — o que é deliberado e certo: um pedido que o dono ainda
   não olhou não pode travar o objeto para os outros. Mas `aceitarLocacao()` é um
   `UPDATE situacao = 'ACEITO'` puro, sem repetir a verificação. Nada impede o
   dono de aceitar dois pedidos sobrepostos. **A trava protege contra o segundo
   pedido, não contra o segundo aceite.**

Se eu refizesse hoje: uma transação com `SELECT ... FOR UPDATE` sobre as
locações do objeto, o teste de sobreposição pelas quatro pontas, e a mesma
verificação repetida no aceite.

---

## O ciclo de uma locação

Toda a máquina de estados está em seis `UPDATE`s de uma coluna (`situacao`) em
`dao/LocaçãoDAO.java`:

```
                         (o locatário pede)
                                 │
                                 ▼
                          ┌─────────────┐
              ┌───────────│ SOLICITADO  │───────────┐
              │           └─────────────┘           │
    dono recusa │                 │ dono aceita     │ locatário cancela
              ▼                   ▼                 ▼
       ┌────────────┐      ┌────────────┐    ┌────────────┐
       │ REJEITADO  │      │   ACEITO   │    │ CANCELADO  │
       └────────────┘      └────────────┘    └────────────┘
                                 │
                                 │ locatário confirma que recebeu o item
                                 ▼
                          ┌───────────────┐
                          │ EM ANDAMENTO  │
                          └───────────────┘
                                 │
                                 │ devolução
                                 ▼
                          ┌───────────────┐
                          │  FINALIZADO   │──► libera as duas avaliações
                          └───────────────┘
```

`REJEITADO`, `CANCELADO` e `SOLICITADO` são exatamente os três estados que a
trava de agenda ignora: nenhum deles reserva dias no calendário do objeto.

Cada lado tem sua tela e vê a mesma locação de um ângulo:

- **`meusPedidos.xhtml`** — o que *eu pedi*, em abas por situação. Aqui cancelo,
  confirmo o recebimento e avalio no fim.
- **`calendarioItem.xhtml`** — a agenda de *um item meu*, também em abas. Aqui
  aceito, recuso, marco devolução e respondo perguntas.

---

## Reputação: a média das duas pontas

Cada locação finalizada produz até três notas: o objeto, o locador e o
locatário. A nota do usuário é a **média das médias**, calculada em
`inserirOpiniaoparaDono` / `inserirOpiniaoparaLocador`:

- média das notas que ele recebeu **como dono de item**;
- média das notas que ele recebeu **como quem alugou**;
- se as duas existem, a reputação é a média entre elas; se só uma existe, é ela.

Fazer a média entre as duas médias, e não a média simples de todas as notas,
tem um efeito específico: quem alugou trinta vezes e emprestou uma vez não
esconde a nota de dono debaixo do volume de locatário. As duas metades pesam
igual.

Objeto sem nenhuma nota não mostra `0,0` — mostra "sem classificação". A
diferença entre "ninguém avaliou" e "avaliaram mal" é tratada explicitamente
(`dao/ObjetoDAO.java`, no preenchimento de `mensMedia`).

⚠️ **Os nomes dos dois lados estão trocados no código.** A tela do locatário
grava em `classificacaoItem` + `classificacaoLocatario` chamando
`salvarOpiniaoLocador()`; a tela do dono grava em `classificacaoLocador`
chamando `salvarOpiniaoLocatario()`. E `mediaLocat` é agregada por
`objeto.idUsuario` (ou seja, mede o usuário **como dono**), enquanto `mediaLocad`
é agregada por `locacao.idUsuario` (mede o usuário **como locatário**). A
reputação final soma as duas metades e por isso continua sendo "a nota deste
usuário nos dois papéis" — mas qualquer leitura de uma metade isolada está
invertida em relação ao nome dela. Está documentado aqui em vez de corrigido
porque o repositório é o de 2021.

---

## Arquitetura

Três camadas, sem framework de persistência, sem injeção de dependência:

```
web/*.xhtml            25 telas + 6 templates (Facelets)
  │                    JSF 2 + PrimeFaces + Materialize CSS
  ▼
controle/*.java        6 @ManagedBean @SessionScoped
  │                    fluxo de tela, cálculo de preço, montagem de listas
  ▼
dao/*.java             6 DAOs estáticos, SQL escrito à mão
  │                    JDBC puro, Connection por método
  ▼
MySQL                  6 tabelas: usuario, objeto, locacao,
                       categoria, tipopreco, mensagem
```

Apoio em `util/`: `AutorizacaoFilter` (um `@WebFilter("*.xhtml")` com matriz de
URLs permitidas por perfil), `SessionContext` (fachada sobre o mapa de sessão do
JSF), `Conexao` (fábrica de `Connection`) e `Impressao` (JasperReports → PDF).

| Peça | Escolha | Observação |
|---|---|---|
| Linguagem | Java 8 | `javac.source`/`target` = 1.8 |
| View | JSF 2 + Facelets + PrimeFaces | `javax.faces.*`, namespace `primefaces.org/ui` |
| CSS | Materialize + Bootstrap + Font Awesome | responsivo; ~9 commits só de responsividade |
| Persistência | JDBC + `PreparedStatement` | sem JPA/Hibernate, sem pool |
| Senha | `md5(?)` no próprio SQL | ver Limitações |
| Servidor | Tomcat | `j2ee.server.type=Tomcat`, `j2ee.platform=1.7-web` |
| Build | Ant / NetBeans | `build.xml` + `nbproject/`, gera `TCC.war` |
| CEP | API pública ViaCEP | biblioteca de terceiros embutida em `src/java/ViaCep/` |
| Relatório | JasperReports 6.6 | `web/relatorios/ValorGanho.jasper` |

Em números: 28 arquivos `.java`, ~6.900 linhas, das quais ~2.700 são os DAOs.
Não há teste automatizado.

### Duas escolhas menores que valem menção

**Endereço vem do CEP, não da digitação.** Cadastro de usuário, cadastro de item
e cadastro de locação chamam `cepConfirma()`, que consulta o ViaCEP e preenche
UF, cidade, bairro e logradouro. Sobra ao usuário o número e o complemento. O
endereço de retirada é gravado *na locação*, não herdado do item — o dono pode
combinar entrega em outro lugar, e o histórico guarda onde aquela entrega
específica aconteceu.

**Busca por CEP é o filtro que importa.** Alugar uma furadeira de alguém a
quarenta quilômetros não faz sentido; a barra de busca combina texto, categoria e
CEP, e o próprio usuário pode jogar seu CEP no filtro com um botão ("Usar meu
CEP"). É comparação de igualdade de CEP, não raio de distância — o que é a
limitação óbvia e conhecida dessa escolha.

---

## As telas

| Tela | O que faz |
|---|---|
| `paginaInicial` / `paginaInicialSemLogin` / `paginaInicial_adm` | vitrine de itens, busca por texto/categoria/CEP |
| `itemSelecionado` | ficha do item: fotos, preço, condição, avaliações, perguntas e respostas |
| `cadastroItens` / `manutencaoItens` | cadastro e lista dos meus itens |
| `calendarioItem` | agenda de um item meu, em abas por situação |
| `cadastroAluguel` | pedido de locação: datas, endereço de retirada, cálculo do preço |
| `meusPedidos` | o que eu pedi, em abas por situação |
| `infoPedido` / `infoPedidoItem` | detalhe de uma locação, dos dois lados |
| `usuarioSelecionado` / `meuPerfil` | reputação, itens e histórico de um usuário |
| `relatorios` | locações finalizadas de um item, filtro por período, total ganho |
| `administrador` + `manutencao{Categorias,TipoPrecos,Usuarios}` | área administrativa |

Perguntas e respostas na ficha do item são uma autorrelação: `Mensagem` tem
`perguntaPai`, então resposta é uma mensagem que aponta para outra.

---

## Como rodar

**Aviso primeiro: este repositório não sobe como está.** São duas coisas
faltando, ambas conhecidas e explicadas abaixo. Deixo o passo a passo real em vez
de um "mvn install" que não existe.

Você vai precisar de JDK 8, Tomcat, MySQL e Ant (ou o NetBeans, que é onde o
projeto foi feito).

**1. Reconstruir `src/java/util/Conexao.java`.** O commit `3f6ae68`
("without database path") tirou a string de conexão do arquivo e deixou a
chamada `DriverManager.getConnection(` sem argumento e sem fechar — o arquivo,
como está publicado, **não compila**. Restaure para algo como:

```java
return DriverManager.getConnection(
        "jdbc:mysql://localhost/queroemprestar?useTimezone=true&serverTimezone=UTC",
        System.getenv("DB_USER"), System.getenv("DB_PASS"));
```

**2. Criar o schema.** Não há dump SQL no repositório — é a maior lacuna dele.
As seis tabelas e todas as colunas são recuperáveis lendo os DAOs, que escrevem
SQL literal; comece por `UsuarioDAO.inserir`, `ObjetoDAO.inserir` e
`LocaçãoDAO.inserir`. Ao criar, preste atenção em três pontos que o código
pressupõe:

- `locacao.dataInicial` e `locacao.dataFinal` **precisam ser `NOT NULL`** — é o
  que faz a trava de agenda funcionar (ver acima);
- `locacao.situacao` precisa de `DEFAULT 'SOLICITADO'`, porque o `INSERT` não
  preenche essa coluna;
- `usuario.status` e `objeto.status` usam `'ATIVO'`/`'INATIVO'`, e
  `usuario.adm` também — o mesmo par de strings marca "é administrador".

**3. Bibliotecas.** PrimeFaces, JSF 2 e JSTL entram pelas *libraries* do NetBeans
(`libs.primefaces.classpath`, `libs.jsf20.classpath`, `libs.jstl.classpath` em
`nbproject/project.properties`) e **não estão no repositório**. Fora do NetBeans,
coloque os jars no classpath à mão. O resto (MySQL Connector 8.0.18,
JasperReports 6.6, commons-*) está versionado em `lib/`.

**4. Upload de imagem.** `ObjetoControle.doUpload()` grava em um caminho absoluto
fixo, herdado da máquina em que o projeto foi escrito. Troque por um diretório
configurável antes de testar o cadastro de item com foto.

**5. Build e deploy.**

```bash
ant dist          # gera dist/TCC.war
# copie o war para <tomcat>/webapps/
```

**6. Abrir.** O `welcome-file` do `web.xml` aponta para `faces/index.xhtml`, que
não existe (o `index.html` na raiz é uma página solta de validação de CPF, de
outro exercício). A porta de entrada real é:

```
http://localhost:8080/TCC/faces/login.xhtml
```

ou `faces/paginaInicialSemLogin.xhtml` para ver a vitrine sem conta.

---

## Limitações conhecidas

São de 2021 e estão registradas, não corrigidas. Algumas eu só enxerguei
relendo o código agora.

**Segurança**

- **Senha em MD5 sem sal**, calculado pelo próprio MySQL (`md5(?)` dentro do
  `INSERT` e do `SELECT` de login, em `dao/UsuarioDAO.java`). MD5 já era
  inadequado para senha em 2021. Hoje seria bcrypt/Argon2 na aplicação, nunca no
  banco — o hash calculado no servidor de banco aparece em log de query.
- **Injeção de SQL na busca por CEP.** `ObjetoDAO.getListaPesquisaCep` concatena
  o CEP digitado direto na cláusula `where o.cep = ` + `cep`, sem aspas e sem
  parâmetro. O termo de busca textual (`pesq`) está corretamente parametrizado;
  o CEP escapou. É o bug mais sério do repositório.
- **Autorização por lista de URLs.** `util/AutorizacaoFilter` compara o fim da
  URI contra uma matriz de caminhos permitidos por perfil. Funciona, mas é uma
  lista que precisa ser editada toda vez que nasce uma tela — tela nova esquecida
  ali cai no `else` e redireciona. Pior: o caso do visitante anônimo é tratado
  por `catch (NullPointerException)`, ou seja, exceção como fluxo de controle.
- **O botão "Controle" do administrador é escondido com `opacity: 0`** na home do
  usuário comum. A proteção de verdade é o filtro (e ela existe), mas esconder
  com CSS convida a achar que esconder basta.

**Funcionalidade**

- **Locação de um único dia sai R$ 0,00.** O preço é
  `(dataFinal - dataInicial)` em dias inteiros × preço; mesmo dia de retirada e
  devolução dá zero. A divisão é inteira em milissegundos, então também trunca
  frações.
- **A validação de datas é só no navegador.** `minDate: new Date()` é opção do
  datepicker do Materialize; nada no servidor impede data no passado ou data
  final anterior à inicial.
- **O relatório em PDF não está ligado a nenhuma tela.** `UsuarioControle.imprimir()`
  e `util/Impressao` existem e o `ValorGanho.jasper` está compilado no
  repositório, mas nenhum `.xhtml` chama o método — a tela `relatorios.xhtml`
  mostra tabelas em HTML, não gera PDF. O `.jrxml` (fonte do relatório) também
  não foi versionado, só o `.jasper` compilado, então o relatório não é editável
  a partir daqui.
- **`Impressao` abre um `JOptionPane`** quando a geração falha — uma caixa de
  diálogo Swing, num servidor web. Ninguém nunca a veria; o servidor é que
  travaria esperando.
- **Sem transação, sem pool.** Cada método de DAO abre e fecha sua própria
  `Connection`. As operações que fazem vários `UPDATE`s em sequência (gravar
  avaliação e recalcular médias, por exemplo) não são atômicas.
- `UsuarioDAO.getLista()` preenche estado e cidade com as strings literais
  `"estado"` e `"cidade"` em vez das colunas — bug visível na lista de usuários
  do administrador.
- Em `cadastroAluguel.xhtml`, o primeiro inicializador do datepicker usa uma
  variável `options` que nunca é declarada; quem de fato inicializa o campo é o
  bloco jQuery seguinte.

**Dívida no próprio repositório**

São ~5.800 arquivos e ~117 MB de conteúdo versionado (o GitHub reporta ~40 MB
empacotados). Quase nada disso é código:

- **`lib/` (68 MB, 3.442 arquivos)** tem os jars de dependência — e, junto,
  jars que são do JRE, não do projeto: `resources.jar`, `jsse.jar`, `jce.jar`,
  `javaws.jar`, `deploy.jar`, `plugin.jar`, `jfr.jar`, `management-agent.jar`.
  Alguém arrastou a pasta `lib` da instalação do Java para dentro do projeto.
  `commons-email` está versionado e nunca é importado.
- **`build/` (31 MB)** e **`dist/javadoc/`** são saída de compilação versionada.
- O `.gitignore` já ignora `*.jar` — foi adicionado *depois* dos jars entrarem.
- `.gitattributes` contém `*.css linguist-language=Java`, o que faz o GitHub
  contar folhas de estilo como Java. A barra de linguagens do repositório está,
  por isso, errada.
- As classes `Locação` e `LocaçãoDAO` têm acento no nome do arquivo e da classe.
  Compila (o projeto é UTF-8), mas é um convite a problema em qualquer pipeline
  com locale diferente.

Nada disso foi limpo porque reescrever a história do repositório apagaria a
cronologia real do trabalho, que é justamente o que ele documenta.

---

## Créditos

- **ViaCEP** (`src/java/ViaCep/`) — cliente Java da API pública de CEP, de
  autoria de terceiros, mantido no repositório com o cabeçalho original.
- **`util/Impressao`** — classe utilitária de geração de PDF com JasperReports,
  de autoria de terceiros, também com o cabeçalho original preservado.
- Materialize CSS, Bootstrap, Font Awesome e os templates de formulário Colorlib
  compõem a interface.

## Licença

MIT. Ver [LICENSE](LICENSE).
