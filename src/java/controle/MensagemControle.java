package controle;

import dao.MensagemDAO;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.Mensagem;
import modelo.Usuario;
import modelo.Objeto;
import modelo.Mensagem;
import util.SessionContext;

@ManagedBean
@SessionScoped
public class MensagemControle {

    private List<Mensagem> lista;
    private List<Mensagem> listaObjeto;
    private List<Mensagem> listaObjetoPerguntas;
    private List<Mensagem> listaObjetoRespostas;
    private List<Mensagem> ListaObjetoPerguntasMens;
    private Mensagem mensagem = new Mensagem();
    private boolean salvar = false;
    private Objeto objeto = new Objeto();
    private int idUsuario;
    private int idObjeto;
    private int idPerguntaPai;
    private Usuario usuario;

    public String preparaIncluir() {
        setMensagem(new Mensagem());
        salvar = true;
        mensagem.setUsuario(SessionContext.getInstance().getUsuarioLogado());
        return "itemSelecionado.xhtml?faces-redirect=true";
        //return "cadastroMensagens.xhtml?faces-redirect=true";
    }

    public String preparaAlterar() {
        salvar = false;
        idUsuario = mensagem.getUsuario().getIdUsuario();
        setIdObjeto(mensagem.getObjeto().getIdObjeto());
        setIdPerguntaPai(mensagem.getPerguntaPai().getIdMensagem());
        return "cadastroMensagens.xhtml?faces-redirect=true";
    }

    public String preparaResposta(Mensagem me) {
        salvar = true;
        mensagem = me;
        SessionContext.getInstance().setAttribute("mensagem", mensagem);
        return "";
    }

    @PostConstruct
    public void atualizaLista() {
        try {
            lista = MensagemDAO.getLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @PostConstruct
    public void atualizaListaObjeto() {
        try {
            setListaObjeto(MensagemDAO.getListaObjeto((Objeto) SessionContext.getInstance().getAttribute("objeto")));
            setListaObjetoPerguntas(MensagemDAO.getListaObjetoPerguntas((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String salvarPergunta() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);

        objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");

        mensagem.setUsuario(usuario);
        mensagem.setObjeto(objeto);

        salvar = true;
        try {
            if (salvar) {
                MensagemDAO.inserirPergunta(mensagem);
            } else {
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaListaObjetoMensagem();
        return "itemSelecionado.xhtml?faces-redirect=true";
    }

    public void atualizaListaObjetoMensagem() {
        try {
            setListaObjetoPerguntasMens(MensagemDAO.getListaObjetoPerguntas((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String salvarResposta() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);

        Objeto objeto = new Objeto();
        objeto.setIdObjeto(idObjeto);

        Mensagem perguntaPai = new Mensagem();
        perguntaPai.setIdMensagem(idPerguntaPai);

        perguntaPai = (Mensagem) SessionContext.getInstance().getAttribute("mensagem");
        objeto = perguntaPai.getObjeto();

        mensagem.setUsuario(usuario);
        mensagem.setObjeto(objeto);
        mensagem.setPerguntaPai(perguntaPai);

        try {
            if (salvar) {
                MensagemDAO.inserirResposta(mensagem);
            } else {
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void excluir() {
        try {
            MensagemDAO.excluir(getMensagem());
            atualizaLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Mensagem> getLista() {
        return lista;
    }

    public void setLista(List<Mensagem> lista) {
        this.lista = lista;
    }

    public boolean isSalvar() {
        return salvar;
    }

    public void setSalvar(boolean salvar) {
        this.salvar = salvar;
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
     * @return the idObjeto
     */
    public int getIdObjeto() {
        return idObjeto;
    }

    /**
     * @param idObjeto the idObjeto to set
     */
    public void setIdObjeto(int idObjeto) {
        this.idObjeto = idObjeto;
    }

    /**
     * @return the idPerguntaPai
     */
    public int getIdPerguntaPai() {
        return idPerguntaPai;
    }

    /**
     * @param idPerguntaPai the idPerguntaPai to set
     */
    public void setIdPerguntaPai(int idPerguntaPai) {
        this.idPerguntaPai = idPerguntaPai;
    }

    /**
     * @return the listaObjeto
     */
    public List<Mensagem> getListaObjeto() {
        return listaObjeto;
    }

    /**
     * @param listaObjeto the listaObjeto to set
     */
    public void setListaObjeto(List<Mensagem> listaObjeto) {
        this.listaObjeto = listaObjeto;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the listaObjetoPerguntas
     */
    public List<Mensagem> getListaObjetoPerguntas() {
        return listaObjetoPerguntas;
    }

    /**
     * @param listaObjetoPerguntas the listaObjetoPerguntas to set
     */
    public void setListaObjetoPerguntas(List<Mensagem> listaObjetoPerguntas) {
        this.listaObjetoPerguntas = listaObjetoPerguntas;
    }

    /**
     * @return the listaObjetoRespostas
     */
    public List<Mensagem> getListaObjetoRespostas() {
        return listaObjetoRespostas;
    }

    /**
     * @param listaObjetoRespostas the listaObjetoRespostas to set
     */
    public void setListaObjetoRespostas(List<Mensagem> listaObjetoRespostas) {
        this.listaObjetoRespostas = listaObjetoRespostas;
    }

    /**
     * @return the ListaObjetoPerguntasMens
     */
    public List<Mensagem> getListaObjetoPerguntasMens() {
        return ListaObjetoPerguntasMens;
    }

    /**
     * @param ListaObjetoPerguntasMens the ListaObjetoPerguntasMens to set
     */
    public void setListaObjetoPerguntasMens(List<Mensagem> ListaObjetoPerguntasMens) {
        this.ListaObjetoPerguntasMens = ListaObjetoPerguntasMens;
    }

    /**
     * @return the perguntaPai
     */
}
