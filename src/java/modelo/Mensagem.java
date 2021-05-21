package modelo;

import java.util.Date;

public class Mensagem {
    private int idMensagem;
    private String descricaoMensagem;
    private String descricaoResp;
    private Date dataHora;
    private Usuario usuario;
    private Objeto objeto;
    private Mensagem perguntaPai;

    /**
     * @return the idMensagem
     */
    public int getIdMensagem() {
        return idMensagem;
    }

    /**
     * @param idMensagem the idMensagem to set
     */
    public void setIdMensagem(int idMensagem) {
        this.idMensagem = idMensagem;
    }

    /**
     * @return the descricaoMensagem
     */
    public String getDescricaoMensagem() {
        return descricaoMensagem;
    }

    /**
     * @param descricaoMensagem the descricaoMensagem to set
     */
    public void setDescricaoMensagem(String descricaoMensagem) {
        this.descricaoMensagem = descricaoMensagem;
    }

    /**
     * @return the dataHora
     */
    public Date getDataHora() {
        return dataHora;
    }

    /**
     * @param dataHora the dataHora to set
     */
    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
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
     * @return the perguntaPai
     */
    public Mensagem getPerguntaPai() {
        return perguntaPai;
    }

    /**
     * @param perguntaPai the perguntaPai to set
     */
    public void setPerguntaPai(Mensagem perguntaPai) {
        this.perguntaPai = perguntaPai;
    }

    /**
     * @return the descricaoResp
     */
    public String getDescricaoResp() {
        return descricaoResp;
    }

    /**
     * @param descricaoResp the descricaoResp to set
     */
    public void setDescricaoResp(String descricaoResp) {
        this.descricaoResp = descricaoResp;
    }

}
