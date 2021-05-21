package modelo;

public class TipoPreco {
    private int idTipoPreco;
    private String descricaoTipoPreco;

    public int getIdTipoPreco() {
        return idTipoPreco;
    }

    public void setIdTipoPreco(int idTipoPreco) {
        this.idTipoPreco = idTipoPreco;
    }

    public String getDescricaoTipoPreco() {
        return descricaoTipoPreco;
    }

    public void setDescricaoTipoPreco(String descricaoTipoPreco) {
        this.descricaoTipoPreco = descricaoTipoPreco;
    }

    public void setUsuario(Usuario usuarioLogado) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
