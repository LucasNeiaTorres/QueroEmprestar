package controle;

import ViaCep.ViaCEP;
import dao.UsuarioDAO;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import modelo.Usuario;
import util.Impressao;
import util.SessionContext;
import controle.ObjetoControle;
import dao.LocaçãoDAO;
import dao.ObjetoDAO;
import modelo.Categoria;
import modelo.Locação;
import modelo.Objeto;
import ViaCep.ViaCEP;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;

@ManagedBean
@SessionScoped
public class UsuarioControle {

    private List<Usuario> lista;
    private List<Objeto> listaPesq;
    private List<Objeto> listaObjeto;
    private List<Objeto> listaObjetoUsu;
    private List<Locação> listaTotUsu;
    private List<Locação> listaAvalLocat;
    private List<Locação> listaAvalLocad;
    private Usuario usuario = new Usuario();
    private Categoria categoria = new Categoria();
    private Usuario usuarioLogado = new Usuario();
    private boolean salvar = false;
    private String pesq;
    private String cep;
    private String isCPF;

    public String login() {
        try {
            usuarioLogado = UsuarioDAO.getLogin(usuario);
            if (usuarioLogado == null) {
                System.out.println("USUÁRIO NÃO ENCONTRADO!");
                return "";
            } else {
                System.out.println("USUARIO ENCONTRADO");
                SessionContext.getInstance().setAttribute("usuario", usuarioLogado);
                atualizaListaObjetos();
                if (usuarioLogado.getAdm().equals("ATIVO")) {
                    return "paginaInicial_adm.xhtml?faces-redirect=true";
                } else {
                    return "paginaInicial.xhtml?faces-redirect=true";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isCPF(String CPF) throws SQLException {

        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000")
                || CPF.equals("11111111111")
                || CPF.equals("22222222222") || CPF.equals("33333333333")
                || CPF.equals("44444444444") || CPF.equals("55555555555")
                || CPF.equals("66666666666") || CPF.equals("77777777777")
                || CPF.equals("88888888888") || CPF.equals("99999999999")
                || (CPF.length() != 11)) {
            isCPF = "Falso";
            System.out.println("falso");
            return (false);
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48); // converte no respectivo caractere numerico
            }
            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10))) {
                isCPF = "Verdadeiro";
                System.out.println("true");
                return (true);

            } else {
                isCPF = "Falso";
                System.out.println("falso");
                return (false);
            }
        } catch (InputMismatchException erro) {
            isCPF = "Falso";
            System.out.println("falso");
            return (false);
        }
    }

    public static String imprimeCPF(String CPF) {
        return (CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "."
                + CPF.substring(6, 9) + "-" + CPF.substring(9, 11));
    }

    public String redirecionarPaginaInicial() throws SQLException {
        atualizaListaObjetos();
        if (usuarioLogado.getNomeUsuario() == null) {
            return "paginaInicialSemLogin.xhtml?faces-redirect=true";
        } else {
            if (usuarioLogado.getAdm().equals("INATIVO")) {
                return "paginaInicial.xhtml?faces-redirect=true";
            } else {
                return "paginaInicial_adm.xhtml?faces-redirect=true";
            }
        }
    }

    public String redirecionarUsuSele() {
        Objeto objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");
        usuario = objeto.getUsuario();
        atualizaListaInfoUsu();
        return "usuarioSelecionado.xhtml?faces-redirect=true";
    }
    
    public String redirecionarUsuSeleInfo() {
        Locação locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");
        usuario = locacao.getUsuario();
        atualizaListaInfoUsu();
        return "usuarioSelecionado.xhtml?faces-redirect=true";
    }
    
    public String redirecionarUsuLog() {
        usuario = usuarioLogado;
        atualizaListaInfoUsu();
        return "usuarioSelecionado.xhtml?faces-redirect=true";
    }

    public void usarCEP() {
        cep = usuarioLogado.getCep();
        atualizaListaObjetos();
    }

    public String redirecionarPaginaInicialS() {
        if (usuarioLogado == null || usuarioLogado.getAdm().equals("INATIVO")) {
            return "paginaInicial.xhtml?faces-redirect=true";
        } else {
            return "paginaInicial_adm.xhtml?faces-redirect=true";
        }

    }

    public void atualizaListaObjetos() {
        try {
            if (cep == null || cep.isEmpty()) {
                if (categoria.getIdCategoria() != 0) {
                    setListaObjeto(ObjetoDAO.getLista(SessionContext.getInstance().getUsuarioLogado()));
                    listaPesq = ObjetoDAO.getListaPesquisaCat(pesq, SessionContext.getInstance().getUsuarioLogado(), categoria);

                    listaObjeto = listaPesq;
                } else {

                    if (pesq != null) {
                        setListaObjeto(ObjetoDAO.getLista(SessionContext.getInstance().getUsuarioLogado()));
                        listaPesq = ObjetoDAO.getListaPesquisa(pesq, SessionContext.getInstance().getUsuarioLogado());

                        listaObjeto = listaPesq;
                    } else {
                        setListaObjeto(ObjetoDAO.getLista(SessionContext.getInstance().getUsuarioLogado()));
                    }
                }
            } else {
                setListaObjeto(ObjetoDAO.getLista(SessionContext.getInstance().getUsuarioLogado()));
                listaPesq = ObjetoDAO.getListaPesquisaCep(pesq, SessionContext.getInstance().getUsuarioLogado(), categoria, cep);

                listaObjeto = listaPesq;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaInfoUsu() {
        try {
            setListaObjetoUsu(ObjetoDAO.getListaUsuSele(usuario));
            setListaTotUsu(LocaçãoDAO.getListaTotUsu(usuario));
            setListaAvalLocat(LocaçãoDAO.getListaOpiniUsuLocat(usuario));
            setListaAvalLocad(LocaçãoDAO.getListaOpiniUsuLocad(usuario));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String doLogout() {
        SessionContext.getInstance().encerrarSessao();
        return "login.xhtml?faces-redirect=true";
    }

    public String preparaIncluir() {
        usuario = new Usuario();
        salvar = true;
        return "cadastroUsuarios.xhtml?faces-redirect=true";
    }

    public String preparaAlterar() {
        usuario = SessionContext.getInstance().getUsuarioLogado();
        salvar = false;
        return "cadastroUsuarios.xhtml?faces-redirect=true";

    }

//    public void trocaValor(Usuario usuario) {
//        if (usuario.get().toString().equals("false")) {
//            flagAnormal = false;
//            flagNormal = true;
//        } else {
//            flagNormal = false;
//            flagAnormal = true;
//        }
//        FacesContext.getCurrentInstance().renderResponse();
//
//    }
    public String preparaAlterarPerfil(Usuario u) {
        usuario = u;
        salvar = false;
        return "cadastroUsuarios.xhtml?faces-redirect=true";

    }

    public void atualizaLista() {
        try {
            lista = UsuarioDAO.getLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String salvar() throws ParseException {
        try {
            isCPF(usuario.getCpf());
            if (salvar) {
                if (isCPF.equals("Verdadeiro")) {
                    UsuarioDAO.inserir(usuario);
                    atualizaLista();
                    login();
                    return "paginaInicial.xhtml?faces-redirect=true";
                } else {
                    return "";
                }
            } else {
                if (isCPF.equals("Verdadeiro")) {
                    UsuarioDAO.alterar(usuario);
                    return "paginaInicial.xhtml?faces-redirect=true";
                } else {
                    return "";
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void excluir() {
        try {
            UsuarioDAO.excluir(usuario);
            atualizaLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String inativar() throws SQLException {
        usuario = usuarioLogado;
        UsuarioDAO.inativar(usuario);
        doLogout();
        return "manutencaoItens.xhtml?faces-redirect=true";
    }

    public List<Usuario> getLista() {
        return lista;
    }

    public void setLista(List<Usuario> lista) {
        this.lista = lista;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isSalvar() {
        return salvar;
    }

    public void setSalvar(boolean salvar) {
        this.salvar = salvar;
    }

    /**
     * @return the usuarioLogado
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * @param usuarioLogado the usuarioLogado to set
     */
    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void imprimir() {
        try {

            HashMap<String, Object> lista = new HashMap<String, Object>(); //cria uma lista para passar parametro
            String caminho = "/relatorios/ValorGanho.jasper"; //indicar o relatório a ser gerado
            String nome = "Valor_Ganho_PDF"; //indicar aqui o nome do arquivo
            System.out.println(caminho);
            Impressao i = new Impressao();
            i.gerarRelatorio(lista, caminho, nome);//aqui gera o relatorio
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void cepConfirma() {
        ViaCEP viaCep = new ViaCEP();
        try {
            viaCep.buscar(usuario.getCep());
            usuario.setEstado(viaCep.getUf());
            usuario.setCidade(viaCep.getLocalidade());
            usuario.setBairro(viaCep.getBairro());
            usuario.setEndereco(viaCep.getLogradouro());

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * @return the listaObjeto
     */
    public List<Objeto> getListaObjeto() {
        return listaObjeto;
    }

    /**
     * @param listaObjeto the listaObjeto to set
     */
    public void setListaObjeto(List<Objeto> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    /**
     * @return the listaPesq
     */
    public List<Objeto> getListaPesq() {
        return listaPesq;
    }

    /**
     * @param listaPesq the listaPesq to set
     */
    public void setListaPesq(List<Objeto> listaPesq) {
        this.listaPesq = listaPesq;
    }

    /**
     * @return the pesq
     */
    public String getPesq() {
        return pesq;
    }

    /**
     * @param pesq the pesq to set
     */
    public void setPesq(String pesq) {
        this.pesq = pesq;
    }

    /**
     * @return the listaObjetoUsu
     */
    public List<Objeto> getListaObjetoUsu() {
        return listaObjetoUsu;
    }

    /**
     * @param listaObjetoUsu the listaObjetoUsu to set
     */
    public void setListaObjetoUsu(List<Objeto> listaObjetoUsu) {
        this.listaObjetoUsu = listaObjetoUsu;
    }

    /**
     * @return the listaTotUsu
     */
    public List<Locação> getListaTotUsu() {
        return listaTotUsu;
    }

    /**
     * @param listaTotUsu the listaTotUsu to set
     */
    public void setListaTotUsu(List<Locação> listaTotUsu) {
        this.listaTotUsu = listaTotUsu;
    }

    /**
     * @return the listaAvalLocat
     */
    public List<Locação> getListaAvalLocat() {
        return listaAvalLocat;
    }

    /**
     * @param listaAvalLocat the listaAvalLocat to set
     */
    public void setListaAvalLocat(List<Locação> listaAvalLocat) {
        this.listaAvalLocat = listaAvalLocat;
    }

    /**
     * @return the listaAvalLocad
     */
    public List<Locação> getListaAvalLocad() {
        return listaAvalLocad;
    }

    /**
     * @param listaAvalLocad the listaAvalLocad to set
     */
    public void setListaAvalLocad(List<Locação> listaAvalLocad) {
        this.listaAvalLocad = listaAvalLocad;
    }

    /**
     * @return the categoria
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * @param categoria the categoria to set
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * @return the cep
     */
    public String getCep() {
        return cep;
    }

    /**
     * @param cep the cep to set
     */
    public void setCep(String cep) {
        this.cep = cep;
    }

}
