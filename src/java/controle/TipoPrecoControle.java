package controle;

import dao.TipoPrecoDAO;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import modelo.TipoPreco;
import util.SessionContext;

@ManagedBean
@SessionScoped
public class TipoPrecoControle {
    
    private List<TipoPreco> lista;
    private TipoPreco tipoPreco = new TipoPreco();
    private boolean salvar = false;
    
    public String preparaIncluir(){
        tipoPreco = new TipoPreco();
        salvar = true;
        return "cadastroTipoPrecos.xhtml?faces-redirect=true";
    }
    public String preparaAlterar(){
        salvar = false;
        return "cadastroTipoPrecos.xhtml?faces-redirect=true";
    }
    @PostConstruct
    public void atualizaLista(){
        try {
            lista = TipoPrecoDAO.getLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public String salvar(){
        try {
            if (salvar) {
                TipoPrecoDAO.inserir(tipoPreco);
            }else{
                TipoPrecoDAO.alterar(tipoPreco);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        atualizaLista();
        return "manutencaoTipoPrecos.xhtml?faces-redirect=true";
    }

    public void excluir(){
        try {
            TipoPrecoDAO.excluir(tipoPreco);
            atualizaLista();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public List<TipoPreco> getLista() {
        return lista;
    }

    public void setLista(List<TipoPreco> lista) {
        this.lista = lista;
    }

    public TipoPreco getTipoPreco() {
        return tipoPreco;
    }

    public void setTipoPreco(TipoPreco tipoPreco) {
        this.tipoPreco = tipoPreco;
    }

    public boolean isSalvar() {
        return salvar;
    }

    public void setSalvar(boolean salvar) {
        this.salvar = salvar;
    }
    
}