package controle;

import ViaCep.ViaCEP;
import dao.LocaçãoDAO;
import dao.ObjetoDAO;
import java.sql.SQLException;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.Categoria;
import modelo.Mensagem;
import modelo.Objeto;
import modelo.TipoPreco;
import modelo.Usuario;
import dao.MensagemDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.servlet.http.Part;
import modelo.Locação;
import org.primefaces.model.UploadedFile;
import util.SessionContext;

@ManagedBean
@SessionScoped
public class ObjetoControle {

    private List<Objeto> lista;
    private List<Objeto> listaUsuario;
    private List<Objeto> media;
    private List<Locação> listaOpinioes;
    private List<Locação> totalLoc;
    private Objeto objeto = new Objeto();
    private Mensagem mensagem;
    private boolean salvar = false;
    private int idCategoria;
    private int idTipoPreco;
    private int idUsuario;
    private UploadedFile file;
    private Part image;
    private boolean upladed;
    private List<Mensagem> listaObjetoMens;
    private List<Mensagem> listaObjetoPerguntasMens;
    private String mensMedia;

    public List<Objeto> getListaUsuario() {
        return listaUsuario;
    }

    public void setListaUsuario(List<Objeto> listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    public String preparaIncluir() {
        objeto = new Objeto();
        salvar = true;
        idCategoria = 0;
        idTipoPreco = 0;
        idUsuario = 0;
        objeto.setUsuario(SessionContext.getInstance().getUsuarioLogado());
        return "cadastroItens.xhtml?faces-redirect=true";

    }

    public String preparaAlterar() {
        salvar = false;
        idCategoria = objeto.getCategoria().getIdCategoria();
        idTipoPreco = objeto.getTipoPreco().getIdTipoPreco();
        idUsuario = objeto.getUsuario().getIdUsuario();

        return "cadastroItens.xhtml?faces-redirect=true";

    }

    public String redirecionarRelatorios() {
        objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");
        return "relatorios.xhtml?faces-redirect=true";
    }

    public String redirecionarMeusItens() {
        if (SessionContext.getInstance().getAttribute("usuario") == null) {
            return "login.xhtml?faces-redirect=true";
        } else {
            atualizaListaUsuario();
            return "manutencaoItens.xhtml?faces-redirect=true";
        }
    }

    public String preparaVisualizacao(Objeto i) {
        objeto = i;
        salvar = true;
        SessionContext.getInstance().setAttribute("objeto", objeto);
        setMensagem(new Mensagem());
        getMensagem().setUsuario(SessionContext.getInstance().getUsuarioLogado());
        getMensagem().setObjeto(objeto);
        atualizaListaObjetoMensagem();
        atualizaMedia();
        atualizaListaOpinioes();
        atualizaListaTotObj();
        return "itemSelecionado.xhtml?faces-redirect=true";
    }

    public String salvarPergunta() {
        if (SessionContext.getInstance().getAttribute("usuario") == null) {
            return "login.xhtml?faces-redirect=true";
        } else {
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
    }

    public void atualizaListaObjetoMensagem() {
        try {
            setListaObjetoPerguntasMens(MensagemDAO.getListaObjetoPerguntas((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaOpinioes() {
        try {
            setListaOpinioes(LocaçãoDAO.getListaOpini((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaTotObj() {
        try {
            setTotalLoc(LocaçãoDAO.getListaTotObjt((Objeto) SessionContext.getInstance().getAttribute("objeto")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaMedia() {
        try {
            objeto = (Objeto) SessionContext.getInstance().getAttribute("objeto");
            media = ObjetoDAO.getMediaAval(objeto);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaLista() {
        try {
            lista = ObjetoDAO.getLista(SessionContext.getInstance().getUsuarioLogado());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void atualizaListaUsuario() {
        try {
            listaUsuario = ObjetoDAO.getListaUsuario(SessionContext.getInstance().getUsuarioLogado());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String salvar() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);

        TipoPreco tipoPreco = new TipoPreco();
        tipoPreco.setIdTipoPreco(idTipoPreco);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);

        objeto.setTipoPreco(tipoPreco);
        objeto.setCategoria(categoria);
        objeto.setUsuario(usuario);
        try {
            if (salvar) {
                ObjetoDAO.inserir(objeto);
            } else {
                ObjetoDAO.alterar(objeto);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaListaUsuario();
        return "manutencaoItens.xhtml?faces-redirect=true";
    }

    public void excluir() {
        try {
            ObjetoDAO.excluir(objeto);
            atualizaLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void doUpload() {
        try {
            InputStream in = image.getInputStream();

            File f = new File("E:/TCC - Lucas Néia/TCC/web/ImagensObjetos/" + image.getSubmittedFileName());
             // Use your image path
       
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.close();
            in.close();

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("path", f.getAbsolutePath());
            upladed = true;

            objeto.setImagem("ImagensObjetos/" + image.getSubmittedFileName());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

    }

    public String inativar() throws SQLException {
        if (salvar == false) {
            ObjetoDAO.inativar(objeto);
        }

        return "manutencaoItens.xhtml?faces-redirect=true";
    }

    public void usarMinhaLoc() {
        objeto.setCep(SessionContext.getInstance().getUsuarioLogado().getCep());
        objeto.setEstado(SessionContext.getInstance().getUsuarioLogado().getEstado());
        objeto.setCidade(SessionContext.getInstance().getUsuarioLogado().getCidade());
        objeto.setBairro(SessionContext.getInstance().getUsuarioLogado().getBairro());
        objeto.setEndereco(SessionContext.getInstance().getUsuarioLogado().getEndereco());
        objeto.setNumeroResidencia(SessionContext.getInstance().getUsuarioLogado().getNumeroResidencia());
        objeto.setComplemento(SessionContext.getInstance().getUsuarioLogado().getComplemento());
    }

    public void cepConfirma() {
        ViaCEP viaCep = new ViaCEP();
        try {
            viaCep.buscar(objeto.getCep());
            objeto.setEstado(viaCep.getUf());
            objeto.setCidade(viaCep.getLocalidade());
            objeto.setBairro(viaCep.getBairro());
            objeto.setEndereco(viaCep.getLogradouro());

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public List<Objeto> getLista() {
        return lista;
    }

    public void setLista(List<Objeto> lista) {
        this.lista = lista;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
    }

    public boolean isSalvar() {
        return salvar;
    }

    public void setSalvar(boolean salvar) {
        this.salvar = salvar;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdTipoPreco() {
        return idTipoPreco;
    }

    public void setIdTipoPreco(int idTipoPreco) {
        this.idTipoPreco = idTipoPreco;
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
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Part getImage() {
        return image;
    }

    public void setImage(Part image) {
        this.image = image;
    }

    public boolean isUpladed() {
        return upladed;
    }

    public void setUpladed(boolean upladed) {
        this.upladed = upladed;
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
     * @return the media
     */
    public List<Objeto> getMedia() {
        return media;
    }

    /**
     * @param media the media to set
     */
    public void setMedia(List<Objeto> media) {
        this.media = media;
    }

    /**
     * @return the mensMedia
     */
    public String getMensMedia() {
        return mensMedia;
    }

    /**
     * @param mensMedia the mensMedia to set
     */
    public void setMensMedia(String mensMedia) {
        this.mensMedia = mensMedia;
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

    /**
     * @return the totalLoc
     */
    public List<Locação> getTotalLoc() {
        return totalLoc;
    }

    /**
     * @param totalLoc the totalLoc to set
     */
    public void setTotalLoc(List<Locação> totalLoc) {
        this.totalLoc = totalLoc;
    }
}
