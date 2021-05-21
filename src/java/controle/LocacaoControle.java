package controle;

import ViaCep.ViaCEP;
import dao.LocaçãoDAO;
import dao.MensagemDAO;
import dao.ObjetoDAO;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.Locação;
import modelo.Mensagem;
import modelo.Objeto;
import modelo.Usuario;
import org.primefaces.context.RequestContext;
import util.SessionContext;

@ManagedBean
@SessionScoped
public class LocacaoControle {

    private List<Locação> listaFiltro;
    private List<Locação> media;
    private List<Locação> soma;
    private List<Locação> lista;
    private List<Locação> listaHistorico;
    private List<Locação> listaUsuario;
    private List<Locação> listaPedidosAguardando;
    private List<Locação> listaPedidosAceitos;
    private List<Locação> listaPedidosEmAndamento;
    private List<Locação> listaPedidosDevolvidos;
    private List<Locação> listaPedidosRejeitados;
    private List<Locação> listaPedidosObjeto;
    private List<Locação> listaPedidosObj;
    private List<Locação> listaAceitosObj;
    private List<Locação> listaEmAndamentoObj;
    private List<Locação> listaDevolvidosObj;
    private List<Locação> listaOpinioes;
    private Locação locacao = new Locação();
    private Objeto objeto = new Objeto();
    private boolean salvar = false;
    private int idObjeto;
    private int idUsuario;
    private double preco;
    private String situacao;
    private Usuario usuario;
    private String dt1;
    private String dt2;
    private Mensagem mensagem = new Mensagem();
    private List<Mensagem> listaObjetoMens;
    private List<Mensagem> listaObjetoPerguntasMens;
    private List<Mensagem> listaObjetoRespostasMens;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String preparaModal(Locação al) {
        locacao = al;
        SessionContext.getInstance().setAttribute("locacao", locacao);
        return "";
    }

    public String preparaIncluir() {
        locacao = new Locação();
        salvar = true;
        idObjeto = 0;
        locacao.setUsuario(SessionContext.getInstance().getUsuarioLogado());
        return "cadastroAluguel.xhtml?faces-redirect=true";

    }

    public String preparaAlugar() {
        locacao = new Locação();
        salvar = true;
        objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");
        usuario = SessionContext.getInstance().getUsuarioLogado();
        preco = objeto.getPreco();

        locacao.setPreco(preco);
        locacao.setObjeto(objeto);
        locacao.setUsuario(usuario);

        return "cadastroAluguel.xhtml?faces-redirect=true";

    }

    public String redirecionarCalendario(Objeto i) {
        objeto = i;
        SessionContext.getInstance().setAttribute("objeto", objeto);
        atualizaListaObjeto();
        atualizaListaObjetoMensagem();
        atualizaListaOpinioes();
        System.out.println("---------------------");
        return "calendarioItem.xhtml?faces-redirect=true";

    }

    public void atualizaListaOpinioes() {
        try {
            setListaOpinioes(LocaçãoDAO.getListaOpini((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String redirecionarPedidos() {
        if (SessionContext.getInstance().getAttribute("usuario") == null) {
            return "login.xhtml?faces-redirect=true";
        } else {
            atualizaListaPedidos();
            return "meusPedidos.xhtml?faces-redirect=true";
        }
    }

    public void atualizaListaObjetoMensagem() {
        try {
            setListaObjetoPerguntasMens(MensagemDAO.getListaObjetoPerguntas((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String infoPedido(Locação al) {
        locacao = al;
        SessionContext.getInstance().setAttribute("locacao", locacao);
        System.out.println(locacao.getIdLocacao());
        System.out.println(locacao.getSituacao());
        System.out.println("---------------------");
        return "infoPedido.xhtml?faces-redirect=true";
    }
    
    public String infoPedidoItem(Locação al) {
        locacao = al;
        SessionContext.getInstance().setAttribute("locacao", locacao);
        System.out.println(locacao.getIdLocacao());
        System.out.println(locacao.getSituacao());
        System.out.println("---------------------");
        return "infoPedidoItem.xhtml?faces-redirect=true";
    }

    public String aceitarPedido() throws SQLException {
        locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");

        LocaçãoDAO.aceitarLocacao(locacao);
        atualizaListaObjeto();
        return "";
    }

    public String pedidoRecebido() throws SQLException {
        locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");

        LocaçãoDAO.locacaoRecebida(locacao);
        atualizaListaPedidos();
        return "";
    }

    public String pedidoDevolvido() throws SQLException {
        locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");

        LocaçãoDAO.locacaoDevolvida(locacao);
        atualizaListaObjeto();
        return "";
    }

    public String rejeitarPedido() throws SQLException {
        locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");

        LocaçãoDAO.recusarLocacao(locacao);
        atualizaListaObjeto();
        return "";
    }

    public String cancelarPedido() throws SQLException {
        locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");

        LocaçãoDAO.cancelarLocacao(locacao);
        System.out.println(locacao.getIdLocacao());
        System.out.println("---------------------");
        atualizaListaPedidos();
        return "";
    }

    public void atualizaRelatoriosObjeto() {
        try {
            objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");
            listaHistorico = LocaçãoDAO.getListaHistoricoObjeto(objeto);
            listaFiltro = LocaçãoDAO.getListaPeriodo(dt1, dt2, objeto);
            soma = LocaçãoDAO.getListaPrecoTotObjeto(objeto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaPedidosObjeto() {
        try {
            setListaPedidosObjeto(LocaçãoDAO.getListaPedidosObjeto(objeto));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaObjeto() {
        try {
            listaPedidosObj = LocaçãoDAO.getPedidosAguardandoObj(objeto);
            listaAceitosObj = LocaçãoDAO.getPedidosAceitosObj(objeto);
            listaEmAndamentoObj = LocaçãoDAO.getPedidosEmAndamentoObj(objeto);
            listaDevolvidosObj = LocaçãoDAO.getPedidosDevolvidosObj(objeto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaPedidos() {
        try {
            setListaPedidosAguardando(LocaçãoDAO.getPedidosAguardando(SessionContext.getInstance().getUsuarioLogado()));
            setListaPedidosAceitos(LocaçãoDAO.getPedidosAceitos(SessionContext.getInstance().getUsuarioLogado()));
            setListaPedidosEmAndamento(LocaçãoDAO.getPedidosEmAndamento(SessionContext.getInstance().getUsuarioLogado()));
            setListaPedidosDevolvidos(LocaçãoDAO.getPedidosDevolvidos(SessionContext.getInstance().getUsuarioLogado()));
            setListaPedidosRejeitados(LocaçãoDAO.getPedidosRecusados(SessionContext.getInstance().getUsuarioLogado()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaMedia() {
        try {
            objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");
            media = LocaçãoDAO.getMediaAval(objeto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void calculoPreco() {
        if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/dia")) {
            locacao.setPreco(locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24)));

        } else if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/semana")) {
            locacao.setPreco((locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24))) / 7);

        } else if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/mês")) {
            locacao.setPreco((locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24))) / 30);

        } else if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/ano")) {
            locacao.setPreco((locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24))) / 365);
        }
    }

    public String preparaAlterar() {
        salvar = false;
        idObjeto = locacao.getObjeto().getIdObjeto();
        idUsuario = locacao.getUsuario().getIdUsuario();
        return "cadastroAluguel.xhtml?faces-redirect=true";

    }

    public void atualizaLista() {
        try {
            lista = LocaçãoDAO.getLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaUsuario() {
        try {
            listaUsuario = (LocaçãoDAO.getMeusPedidos(SessionContext.getInstance().getUsuarioLogado()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String salvar() throws ParseException {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);

        locacao.setObjeto(objeto);
        locacao.setUsuario(usuario);
        if (objeto.getTipoPreco().getDescricaoTipoPreco().equals("p/dia")) {
            locacao.setPreco(objeto.getPreco());
        }
        try {
            LocaçãoDAO.inserir(locacao);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaListaPedidos();
        return "meusPedidos.xhtml?faces-redirect=true";
    }

    public String salvarOpiniaoLocador() throws ParseException {
        try {
            locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");
            System.out.println(locacao.getIdLocacao());

            LocaçãoDAO.inserirOpiniaoparaDono(locacao);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaLista();
        return "";
    }

    public String salvarOpiniaoLocatario() throws ParseException {
        try {
            locacao = (Locação) SessionContext.getInstance().getAttribute("locacao");
            System.out.println(locacao.getIdLocacao());

            LocaçãoDAO.inserirOpiniaoparaLocador(locacao);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaListaPedidosObjeto();
        return "";
    }

    public void excluir() {
        try {
            LocaçãoDAO.excluir(locacao);
            atualizaLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void usarMinhaLoc() {
        locacao.setCep(SessionContext.getInstance().getUsuarioLogado().getCep());
        locacao.setEstadoPegada(SessionContext.getInstance().getUsuarioLogado().getEstado());
        locacao.setCidadePegada(SessionContext.getInstance().getUsuarioLogado().getCidade());
        locacao.setBairroPegada(SessionContext.getInstance().getUsuarioLogado().getBairro());
        locacao.setEnderecoPegada(SessionContext.getInstance().getUsuarioLogado().getEndereco());
        locacao.setNumeroResidencia(SessionContext.getInstance().getUsuarioLogado().getNumeroResidencia());
        locacao.setComplementoPegada(SessionContext.getInstance().getUsuarioLogado().getComplemento());
    }

    public void cepConfirma() {
        ViaCEP viaCep = new ViaCEP();
        try {
            viaCep.buscar(locacao.getCep());
            locacao.setEstadoPegada(viaCep.getUf());
            locacao.setCidadePegada(viaCep.getLocalidade());
            locacao.setBairroPegada(viaCep.getBairro());
            locacao.setEnderecoPegada(viaCep.getLogradouro());

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public List<Locação> getLista() {
        return lista;
    }

    public void setLista(List<Locação> lista) {
        this.lista = lista;
    }

    public Locação getLocacao() {
        return locacao;
    }

    public void setLocacao(Locação locacao) {
        this.locacao = locacao;
    }

    public boolean isSalvar() {
        return salvar;
    }

    public void setSalvar(boolean salvar) {
        this.salvar = salvar;
    }

    public int getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }

    /**
     * @return the idUsuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the listaUsuario
     */
    public List<Locação> getListaUsuario() {
        return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<Locação> listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    /**
     * @return the objeto
     */
    public Objeto getObjeto() {
        return objeto;
    }

    /**
     * @param objeto the objeto to set
     */
    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
    }

    /**
     * @return the preco
     */
    public double getPreco() {
        return preco;
    }

    /**
     * @param preco the preco to set
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * @return the listaPedidosObjeto
     */
    public List<Locação> getListaPedidosObjeto() {
        return listaPedidosObjeto;
    }

    /**
     * @param listaPedidosObjeto the listaPedidosObjeto to set
     */
    public void setListaPedidosObjeto(List<Locação> listaPedidosObjeto) {
        this.listaPedidosObjeto = listaPedidosObjeto;
    }

    /**
     * @return the situacao
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * @param situacao the situacao to set
     */
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    /**
     * @return the listaPedidosAguardando
     */
    public List<Locação> getListaPedidosAguardando() {
        return listaPedidosAguardando;
    }

    /**
     * @param listaPedidosAguardando the listaPedidosAguardando to set
     */
    public void setListaPedidosAguardando(List<Locação> listaPedidosAguardando) {
        this.listaPedidosAguardando = listaPedidosAguardando;
    }

    /**
     * @return the listaPedidosAceitos
     */
    public List<Locação> getListaPedidosAceitos() {
        return listaPedidosAceitos;
    }

    /**
     * @param listaPedidosAceitos the listaPedidosAceitos to set
     */
    public void setListaPedidosAceitos(List<Locação> listaPedidosAceitos) {
        this.listaPedidosAceitos = listaPedidosAceitos;
    }

    /**
     * @return the listaPedidosEmAndamento
     */
    public List<Locação> getListaPedidosEmAndamento() {
        return listaPedidosEmAndamento;
    }

    /**
     * @param listaPedidosEmAndamento the listaPedidosEmAndamento to set
     */
    public void setListaPedidosEmAndamento(List<Locação> listaPedidosEmAndamento) {
        this.listaPedidosEmAndamento = listaPedidosEmAndamento;
    }

    /**
     * @return the listaPedidosDevolvidos
     */
    public List<Locação> getListaPedidosDevolvidos() {
        return listaPedidosDevolvidos;
    }

    /**
     * @param listaPedidosDevolvidos the listaPedidosDevolvidos to set
     */
    public void setListaPedidosDevolvidos(List<Locação> listaPedidosDevolvidos) {
        this.listaPedidosDevolvidos = listaPedidosDevolvidos;
    }

    /**
     * @return the listaPedidosRejeitados
     */
    public List<Locação> getListaPedidosRejeitados() {
        return listaPedidosRejeitados;
    }

    /**
     * @param listaPedidosRejeitados the listaPedidosRejeitados to set
     */
    public void setListaPedidosRejeitados(List<Locação> listaPedidosRejeitados) {
        this.listaPedidosRejeitados = listaPedidosRejeitados;
    }

    /**
     * @return the listaPedidosObj
     */
    public List<Locação> getListaPedidosObj() {
        return listaPedidosObj;
    }

    /**
     * @param listaPedidosObj the listaPedidosObj to set
     */
    public void setListaPedidosObj(List<Locação> listaPedidosObj) {
        this.listaPedidosObj = listaPedidosObj;
    }

    /**
     * @return the listaAceitosObj
     */
    public List<Locação> getListaAceitosObj() {
        return listaAceitosObj;
    }

    /**
     * @param listaAceitosObj the listaAceitosObj to set
     */
    public void setListaAceitosObj(List<Locação> listaAceitosObj) {
        this.listaAceitosObj = listaAceitosObj;
    }

    /**
     * @return the listaEmAndamentoObj
     */
    public List<Locação> getListaEmAndamentoObj() {
        return listaEmAndamentoObj;
    }

    /**
     * @param listaEmAndamentoObj the listaEmAndamentoObj to set
     */
    public void setListaEmAndamentoObj(List<Locação> listaEmAndamentoObj) {
        this.listaEmAndamentoObj = listaEmAndamentoObj;
    }

    /**
     * @return the listaDevolvidosObj
     */
    public List<Locação> getListaDevolvidosObj() {
        return listaDevolvidosObj;
    }

    /**
     * @param listaDevolvidosObj the listaDevolvidosObj to set
     */
    public void setListaDevolvidosObj(List<Locação> listaDevolvidosObj) {
        this.listaDevolvidosObj = listaDevolvidosObj;
    }

    /**
     * @return the listaHistorico
     */
    public List<Locação> getListaHistorico() {
        return listaHistorico;
    }

    /**
     * @param listaHistorico the listaHistorico to set
     */
    public void setListaHistorico(List<Locação> listaHistorico) {
        this.listaHistorico = listaHistorico;
    }

    /**
     * @return the listaFiltro
     */
    public List<Locação> getListaFiltro() {
        return listaFiltro;
    }

    /**
     * @param listaFiltro the listaFiltro to set
     */
    public void setListaFiltro(List<Locação> listaFiltro) {
        this.listaFiltro = listaFiltro;
    }

    /**
     * @return the dt1
     */
    public String getDt1() {
        return dt1;
    }

    /**
     * @param dt1 the dt1 to set
     */
    public void setDt1(String dt1) {
        this.dt1 = dt1;
    }

    /**
     * @return the dt2
     */
    public String getDt2() {
        return dt2;
    }

    /**
     * @param dt2 the dt2 to set
     */
    public void setDt2(String dt2) {
        this.dt2 = dt2;
    }

    /**
     * @return the soma
     */
    public List<Locação> getSoma() {
        return soma;
    }

    /**
     * @param soma the soma to set
     */
    public void setSoma(List<Locação> soma) {
        this.soma = soma;
    }

    /**
     * @return the mensagem
     */
    public Mensagem getMensagem() {
        return mensagem;
    }

    /**
     * @param mensagem the mensagem to set
     */
    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    /**
     * @return the listaObjetoMens
     */
    public List<Mensagem> getListaObjetoMens() {
        return listaObjetoMens;
    }

    /**
     * @param listaObjetoMens the listaObjetoMens to set
     */
    public void setListaObjetoMens(List<Mensagem> listaObjetoMens) {
        this.listaObjetoMens = listaObjetoMens;
    }

    /**
     * @return the listaObjetoPerguntasMens
     */
    public List<Mensagem> getListaObjetoPerguntasMens() {
        return listaObjetoPerguntasMens;
    }

    /**
     * @param listaObjetoPerguntasMens the listaObjetoPerguntasMens to set
     */
    public void setListaObjetoPerguntasMens(List<Mensagem> listaObjetoPerguntasMens) {
        this.listaObjetoPerguntasMens = listaObjetoPerguntasMens;
    }

    /**
     * @return the listaObjetoRespostasMens
     */
    public List<Mensagem> getListaObjetoRespostasMens() {
        return listaObjetoRespostasMens;
    }

    /**
     * @param listaObjetoRespostasMens the listaObjetoRespostasMens to set
     */
    public void setListaObjetoRespostasMens(List<Mensagem> listaObjetoRespostasMens) {
        this.listaObjetoRespostasMens = listaObjetoRespostasMens;
    }

    /**
     * @return the media
     */
    public List<Locação> getMedia() {
        return media;
    }

    /**
     * @param media the media to set
     */
    public void setMedia(List<Locação> media) {
        this.media = media;
    }

    /**
     * @return the listaOpinioes
     */
    public List<Locação> getListaOpinioes() {
        return listaOpinioes;
    }

    /**
     * @param listaOpinioes the listaOpinioes to set
     */
    public void setListaOpinioes(List<Locação> listaOpinioes) {
        this.listaOpinioes = listaOpinioes;
    }
}
