package modelo;

import java.util.Date;


public class Locação {
    
    private int idLocacao;
    private Date dataInicial;
    private Date dataFinal;
    private String situacao;
    private String opiniaoItem;
    private String opiniaoLocatario;
    private String opiniaoLocador;
    private int classificacaoLocador;
    private int classificacaoItem;
    private int classificacaoLocatario;
    private double preco;
    private String cep;
    private String estadoPegada;
    private String cidadePegada;
    private String bairroPegada;
    private String enderecoPegada;
    private String complementoPegada;
    private int numeroResidencia;
    private Objeto objeto;
    private Usuario usuario;
    private double soma;
    
  
    public int getIdLocacao() {
        return idLocacao;
    }

    public void setIdLocacao(int idLocacao) {
        this.idLocacao = idLocacao;
    }


    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
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
     * @return the opiniaoItem
     */
    public String getOpiniaoItem() {
        return opiniaoItem;
    }

    /**
     * @param opiniaoItem the opiniaoItem to set
     */
    public void setOpiniaoItem(String opiniaoItem) {
        this.opiniaoItem = opiniaoItem;
    }

    /**
     * @return the opiniaoLocatario
     */
    public String getOpiniaoLocatario() {
        return opiniaoLocatario;
    }

    /**
     * @param opiniaoLocatario the opiniaoLocatario to set
     */
    public void setOpiniaoLocatario(String opiniaoLocatario) {
        this.opiniaoLocatario = opiniaoLocatario;
    }

    /**
     * @return the classificacaoItem
     */
    public int getClassificacaoItem() {
        return classificacaoItem;
    }

    /**
     * @param classificacaoItem the classificacaoItem to set
     */
    public void setClassificacaoItem(int classificacaoItem) {
        this.classificacaoItem = classificacaoItem;
    }

    /**
     * @return the classificacaoLocatario
     */
    public int getClassificacaoLocatario() {
        return classificacaoLocatario;
    }

    /**
     * @param classificacaoLocatario the classificacaoLocatario to set
     */
    public void setClassificacaoLocatario(int classificacaoLocatario) {
        this.classificacaoLocatario = classificacaoLocatario;
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

    /**
     * @return the estadoPegada
     */
    public String getEstadoPegada() {
        return estadoPegada;
    }

    /**
     * @param estadoPegada the estadoPegada to set
     */
    public void setEstadoPegada(String estadoPegada) {
        this.estadoPegada = estadoPegada;
    }

    /**
     * @return the cidadePegada
     */
    public String getCidadePegada() {
        return cidadePegada;
    }

    /**
     * @param cidadePegada the cidadePegada to set
     */
    public void setCidadePegada(String cidadePegada) {
        this.cidadePegada = cidadePegada;
    }

    /**
     * @return the bairroPegada
     */
    public String getBairroPegada() {
        return bairroPegada;
    }

    /**
     * @param bairroPegada the bairroPegada to set
     */
    public void setBairroPegada(String bairroPegada) {
        this.bairroPegada = bairroPegada;
    }

    /**
     * @return the enderecoPegada
     */
    public String getEnderecoPegada() {
        return enderecoPegada;
    }

    /**
     * @param enderecoPegada the enderecoPegada to set
     */
    public void setEnderecoPegada(String enderecoPegada) {
        this.enderecoPegada = enderecoPegada;
    }

    /**
     * @return the numeroResidencia
     */
    public int getNumeroResidencia() {
        return numeroResidencia;
    }

    /**
     * @param numeroResidencia the numeroResidencia to set
     */
    public void setNumeroResidencia(int numeroResidencia) {
        this.numeroResidencia = numeroResidencia;
    }

    /**
     * @return the soma
     */
    public double getSoma() {
        return soma;
    }

    /**
     * @param soma the soma to set
     */
    public void setSoma(double soma) {
        this.soma = soma;
    }

    /**
     * @return the opiniaoLocador
     */
    public String getOpiniaoLocador() {
        return opiniaoLocador;
    }

    /**
     * @param opiniaoLocador the opiniaoLocador to set
     */
    public void setOpiniaoLocador(String opiniaoLocador) {
        this.opiniaoLocador = opiniaoLocador;
    }

    /**
     * @return the classificacaoLocador
     */
    public int getClassificacaoLocador() {
        return classificacaoLocador;
    }

    /**
     * @param classificacaoLocador the classificacaoLocador to set
     */
    public void setClassificacaoLocador(int classificacaoLocador) {
        this.classificacaoLocador = classificacaoLocador;
    }

    /**
     * @return the complementoPegada
     */
    public String getComplementoPegada() {
        return complementoPegada;
    }

    /**
     * @param complementoPegada the complementoPegada to set
     */
    public void setComplementoPegada(String complementoPegada) {
        this.complementoPegada = complementoPegada;
    }
}
