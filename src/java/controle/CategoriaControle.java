package controle;

import dao.CategoriaDAO;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.Categoria;

@ManagedBean
@SessionScoped
public class CategoriaControle {
    
    private List<Categoria> lista;
    private Categoria categoria = new Categoria();
    private boolean salvar = false;
    
    public String preparaIncluir(){
        categoria = new Categoria();
        salvar = true;
        return "cadastroCategorias.xhtml?faces-redirect=true";
    }
    public String preparaAlterar(){
        salvar = false;
        return "cadastroCategorias.xhtml?faces-redirect=true";
    }
    @PostConstruct
    public void atualizaLista(){
        try {
            lista = CategoriaDAO.getLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public String salvar(){
        try {
            if (salvar) {
                CategoriaDAO.inserir(categoria);
            }else{
                CategoriaDAO.alterar(categoria);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaLista();
        return "manutencaoCategorias.xhtml?faces-redirect=true";
    }

    public void excluir(){
        try {
            CategoriaDAO.excluir(categoria);
            atualizaLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public List<Categoria> getLista() {
        return lista;
    }

    public void setLista(List<Categoria> lista) {
        this.lista = lista;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean isSalvar() {
        return salvar;
    }

    public void setSalvar(boolean salvar) {
        this.salvar = salvar;
    }
  
}