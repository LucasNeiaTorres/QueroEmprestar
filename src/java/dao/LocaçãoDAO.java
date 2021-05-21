package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Objeto;
import modelo.Usuario;
import modelo.Locação;
import modelo.TipoPreco;
import util.Conexao;
import util.SessionContext;

public class LocaçãoDAO {

    public static void inserir2(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "insert into locacao \n"
                + "(dataInicial, dataFinal, preco, cep, estadoPegada, cidadePegada, bairroPegada, enderecoPegada, numeroResidencia, complementoPegada, idObjeto, idUsuario)"
                + "values\n"
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setDate(1, new Date(locacao.getDataInicial().getTime()));
        stmt.setDate(2, new Date(locacao.getDataFinal().getTime()));
        stmt.setDouble(3, locacao.getObjeto().getPreco());
        stmt.setString(4, locacao.getCep());
        stmt.setString(5, locacao.getEstadoPegada());
        stmt.setString(6, locacao.getCidadePegada());
        stmt.setString(7, locacao.getBairroPegada());
        stmt.setString(8, locacao.getEnderecoPegada());
        stmt.setInt(9, locacao.getNumeroResidencia());
        stmt.setString(10, locacao.getComplementoPegada());
        stmt.setInt(11, locacao.getObjeto().getIdObjeto());
        stmt.setInt(12, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void inserir(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/dia")) {
            locacao.setPreco(locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24)));

        } else if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/semana")) {
            locacao.setPreco((locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24))) / 7);

        } else if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/mês")) {
            locacao.setPreco((locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24))) / 30);

        } else if (locacao.getObjeto().getTipoPreco().getDescricaoTipoPreco().equals("p/ano")) {
            locacao.setPreco((locacao.getObjeto().getPreco() * ((locacao.getDataFinal().getTime() - locacao.getDataInicial().getTime()) / (1000 * 60 * 60 * 24))) / 365);
        }
        String sql = "insert into locacao \n"
                + "(dataInicial, dataFinal, preco, cep, estadoPegada, cidadePegada, bairroPegada, enderecoPegada, numeroResidencia, complementoPegada, idObjeto, idUsuario)"
                + "values\n"
                + "(case when (select count(*) from locacao l \n"
                + "where (l.dataInicial between ? and ?) and l.idObjeto = ? and l.situacao != 'REJEITADO' and l.situacao !='CANCELADO' and l.situacao !='SOLICITADO') = 0 then ? end, \n"
                + "case when (select count(*) from locacao l \n"
                + "where (l.dataFinal between ? and ?) and l.idObjeto = ? and l.situacao != 'REJEITADO' and l.situacao !='CANCELADO' and l.situacao !='SOLICITADO') = 0 then ? end, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setDate(1, new Date(locacao.getDataInicial().getTime()));
        stmt.setDate(2, new Date(locacao.getDataFinal().getTime()));
        stmt.setInt(3, locacao.getObjeto().getIdObjeto());
        stmt.setDate(4, new Date(locacao.getDataInicial().getTime()));
        stmt.setDate(5, new Date(locacao.getDataInicial().getTime()));
        stmt.setDate(6, new Date(locacao.getDataFinal().getTime()));
        stmt.setInt(7, locacao.getObjeto().getIdObjeto());
        stmt.setDate(8, new Date(locacao.getDataFinal().getTime()));
        stmt.setDouble(9, locacao.getPreco());
        stmt.setString(10, locacao.getCep());
        stmt.setString(11, locacao.getEstadoPegada());
        stmt.setString(12, locacao.getCidadePegada());
        stmt.setString(13, locacao.getBairroPegada());
        stmt.setString(14, locacao.getEnderecoPegada());
        stmt.setInt(15, locacao.getNumeroResidencia());
        stmt.setString(16, locacao.getComplementoPegada());
        stmt.setInt(17, locacao.getObjeto().getIdObjeto());
        stmt.setInt(18, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void inserirOpiniaoparaLocador(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "opiniaoLocador=?, classificacaoLocador=?\n"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, locacao.getOpiniaoLocador());
        stmt.setInt(2, locacao.getClassificacaoLocador());
        stmt.setInt(3, locacao.getIdLocacao());
        stmt.execute();
        System.out.println(locacao.getUsuario().getIdUsuario());

        Usuario u = locacao.getUsuario();
        System.out.println(u.getIdUsuario());
        //Fazer média das avaliações de quando usuário foi locatário
        sql = "select AVG(l.classificacaoLocatario) as mediaLocat from locacao l  \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "WHERE o.idUsuario = ?";

        stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getUsuario().getIdUsuario());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            u.setMediaLocat(rs.getDouble("mediaLocat"));
        }

        //Fazer média das avaliações de quando usuário foi locador
        sql = "select AVG(l.classificacaoLocador) as mediaLocad from locacao l  \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "WHERE u.idUsuario = ?";

        stmt = con.prepareStatement(sql);
        stmt.setInt(1, u.getIdUsuario());
        rs = stmt.executeQuery();
        while (rs.next()) {
            u.setMediaLocad(rs.getDouble("mediaLocad"));
        }
        //Calcular a média entre as médias, apenas selecionando classificações acima de 0
        if (u.getMediaLocat() > 0 && u.getMediaLocad() > 0) {
            u.setClassificacao((u.getMediaLocat() + u.getMediaLocad()) / 2);
        } else if (u.getMediaLocat() == 0 && u.getMediaLocad() > 0) {
            u.setClassificacao(u.getMediaLocad());
        } else if (u.getMediaLocat() > 0 && u.getMediaLocad() == 0) {
            u.setClassificacao(u.getMediaLocat());
        }

        //Setar a classificação média no Usuário, que é o dono do objeto
        sql = "update usuario set \n"
                + "classificacao = ? \n"
                + "WHERE idUsuario = ?";

        stmt = con.prepareStatement(sql);
        stmt.setDouble(1, u.getClassificacao());
        stmt.setInt(2, u.getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void inserirOpiniaoparaDono(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();

        String sql = "update locacao set \n"
                + "opiniaoItem=?, classificacaoItem=?, opiniaoLocatario=?, classificacaoLocatario=?\n"
                + "WHERE idLocacao=?";

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, locacao.getOpiniaoItem());
        stmt.setInt(2, locacao.getClassificacaoItem());
        stmt.setString(3, locacao.getOpiniaoLocatario());
        stmt.setInt(4, locacao.getClassificacaoLocatario());
        stmt.setInt(5, locacao.getIdLocacao());
        stmt.execute();

        //Setar media de classificação do objeto
        sql = "update objeto set \n"
                + "classificacao = (select AVG(classificacaoItem) from locacao where idObjeto = ?) \n"
                + "WHERE idObjeto = ?";

        stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getObjeto().getIdObjeto());
        stmt.setInt(2, locacao.getObjeto().getIdObjeto());
        stmt.execute();

        Usuario u = new Usuario();
        //Selecionar Usuário dono do objeto
        sql = "select * from usuario u  \n"
                + "left join objeto o on o.idUsuario = u.idUsuario \n"
                + "WHERE o.idObjeto = " + locacao.getObjeto().getIdObjeto();

        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            u.setIdUsuario(rs.getInt("idUsuario"));
        }

        //Fazer média das avaliações de quando usuário foi locatário
        sql = "select AVG(l.classificacaoLocatario) as mediaLocat from locacao l  \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "WHERE o.idUsuario = ?";

        stmt = con.prepareStatement(sql);
        stmt.setInt(1, u.getIdUsuario());
        rs = stmt.executeQuery();

        while (rs.next()) {
            u.setMediaLocat(rs.getDouble("mediaLocat"));
        }

        //Fazer média das avaliações de quando usuário foi locador
        sql = "select AVG(l.classificacaoLocador) as mediaLocad from locacao l  \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "WHERE u.idUsuario = ?";

        stmt = con.prepareStatement(sql);
        stmt.setInt(1, u.getIdUsuario());
        rs = stmt.executeQuery();
        while (rs.next()) {
            u.setMediaLocad(rs.getDouble("mediaLocad"));
        }

        //Calcular a média entre as médias, apenas selecionando classificações acima de 0
        if (u.getMediaLocat() > 0 && u.getMediaLocad() > 0) {
            u.setClassificacao((u.getMediaLocat() + u.getMediaLocad()) / 2);
        } else if (u.getMediaLocat() == 0 && u.getMediaLocad() > 0) {
            u.setClassificacao(u.getMediaLocad());
        } else if (u.getMediaLocat() > 0 && u.getMediaLocad() == 0) {
            u.setClassificacao(u.getMediaLocat());
        }

        //Setar a classificação média no Usuário, que é o dono do objeto
        sql = "update usuario set \n"
                + "classificacao = ? \n"
                + "WHERE idUsuario = ?";

        stmt = con.prepareStatement(sql);
        stmt.setDouble(1, u.getClassificacao());
        stmt.setInt(2, u.getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void alterar(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "dataInicial=?, dataFinal=?, preco=?, cep=?, estadoPegada=?, cidadePegada=?, "
                + "bairroPegada=?, enderecoPegada=?, numeroResidencia=?, idObjeto=?, idUsuario=?\n"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setDate(1, new Date(locacao.getDataInicial().getTime()));
        stmt.setDate(2, new Date(locacao.getDataFinal().getTime()));
        stmt.setDouble(3, locacao.getPreco());
        stmt.setString(4, locacao.getCep());
        stmt.setString(5, locacao.getEstadoPegada());
        stmt.setString(6, locacao.getCidadePegada());
        stmt.setString(7, locacao.getBairroPegada());
        stmt.setString(8, locacao.getEnderecoPegada());
        stmt.setInt(9, locacao.getNumeroResidencia());
        stmt.setInt(10, locacao.getObjeto().getIdObjeto());
        stmt.setInt(11, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.setInt(12, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void excluir(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "delete from locacao WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void aceitarLocacao(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "situacao = 'ACEITO'"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void cancelarLocacao(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "situacao = 'CANCELADO'"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void recusarLocacao(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "situacao = 'REJEITADO'"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void locacaoRecebida(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "situacao = 'EM ANDAMENTO'"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void locacaoDevolvida(Locação locacao) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update locacao set \n"
                + "situacao = 'FINALIZADO'"
                + "WHERE idLocacao=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, locacao.getIdLocacao());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static List<Locação> getLista() throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto ";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setClassificacao(rs.getDouble("classificacao"));

            String str = Double.toString(i.getClassificacao());

            if (rs.getDouble("classificacao") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getMeusPedidos(Usuario usuarioLogado) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setClassificacao(rs.getDouble("classificacao"));
            i.setUsuario(u);

            String str = Double.toString(i.getClassificacao());

            if (rs.getDouble("classificacao") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getAvalSol(Usuario usuarioLogado, Objeto i) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select i.classificacao from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'SOLICITADO' and "
                + "i.idObjeto = " + i.getIdObjeto() + " and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            i.setClassificacao(rs.getDouble("classificacao"));
            String str = Double.toString(i.getClassificacao());
            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }
        }
        return lista;
    }

    public static List<Locação> getAvalRec(Usuario usuarioLogado, Objeto i) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select i.classificacao from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'REJEITADO' and "
                + "i.idObjeto = " + i.getIdObjeto() + " and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            i.setClassificacao(rs.getDouble("classificacao"));
            String str = Double.toString(i.getClassificacao());

            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }
        }
        return lista;
    }

    public static List<Locação> getAvalAce(Usuario usuarioLogado, Objeto i) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select i.classificacao from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'ACEITO' and "
                + "i.idObjeto = " + i.getIdObjeto() + " and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            i.setClassificacao(rs.getDouble("classificacao"));
            String str = Double.toString(i.getClassificacao());

            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }
        }
        return lista;
    }

    public static List<Locação> getAvalAnd(Usuario usuarioLogado, Objeto i) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select i.classificacao from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'EM ANDAMENTO' and "
                + "i.idObjeto = " + i.getIdObjeto() + " and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            i.setClassificacao(rs.getDouble("classificacao"));
            String str = Double.toString(i.getClassificacao());

            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));
            }
        }
        return lista;
    }

    public static List<Locação> getAvalDev(Usuario usuarioLogado, Objeto i) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select i.classificacao from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'FINALIZADO' and "
                + "i.idObjeto = " + i.getIdObjeto() + " and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            i.setClassificacao(rs.getDouble("classificacao"));
            String str = Double.toString(i.getClassificacao());

            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }
        }
        return lista;
    }

    public static List<Locação> getPedidosAguardando(Usuario usuarioLogado) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'SOLICITADO' and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            //t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setCondicao(rs.getString("condicao"));
            i.setTipoPreco(t);
            i.setUsuario(u);
            getAvalSol(usuarioLogado, i);

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosRecusados(Usuario usuarioLogado) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select *, i.classificacao from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'REJEITADO' and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            //t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setCondicao(rs.getString("condicao"));
            i.setTipoPreco(t);
            i.setUsuario(u);
            getAvalRec(usuarioLogado, i);

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosAceitos(Usuario usuarioLogado) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'ACEITO' and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setCondicao(rs.getString("condicao"));
            i.setUsuario(u);
            getAvalAce(usuarioLogado, i);

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);

            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosEmAndamento(Usuario usuarioLogado) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'EM ANDAMENTO' and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setCondicao(rs.getString("condicao"));
            i.setUsuario(u);
            getAvalAnd(usuarioLogado, i);

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);

            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosDevolvidos(Usuario usuarioLogado) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a "
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "where a.situacao = 'FINALIZADO' and "
                + "a.idUsuario = " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setCondicao(rs.getString("condicao"));
            i.setUsuario(u);
            getAvalDev(usuarioLogado, i);

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setSituacao(rs.getString("situacao"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));
            a.setObjeto(i);
            a.setUsuario(u);

            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getListaPedidosObjeto(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "where a.situacao = 'SOLICITADO' and "
                + "a.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("classificacao"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setClassificacao(rs.getDouble("classificacao"));

            a.setUsuario(u);
            a.setObjeto(i);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosAguardandoObj(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao l \n"
                + "left join usuario u on l.idUsuario = u.idUsuario \n"
                + "where l.situacao = 'SOLICITADO' and "
                + "l.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setClassificacao(rs.getDouble("classificacao"));
            u.setTelefone(rs.getString("telefone"));
            u.setEmail(rs.getString("email"));
            u.setEstado(rs.getString("estado"));
            u.setCidade(rs.getString("cidade"));
            u.setBairro(rs.getString("bairro"));
            u.setEndereco(rs.getString("endereco"));
            u.setComplemento(rs.getString("complemento"));
            u.setNumeroResidencia(rs.getInt("numeroResidencia"));

            String str = Double.toString(u.getClassificacao());

            if (u.getClassificacao() == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));
            }

            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosAceitosObj(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao l \n"
                + "left join usuario u on l.idUsuario = u.idUsuario \n"
                + "where l.situacao = 'ACEITO' and "
                + "l.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setClassificacao(rs.getDouble("classificacao"));
            u.setTelefone(rs.getString("telefone"));
            u.setEmail(rs.getString("email"));
            u.setEstado(rs.getString("estado"));
            u.setCidade(rs.getString("cidade"));
            u.setBairro(rs.getString("bairro"));
            u.setEndereco(rs.getString("endereco"));
            u.setComplemento(rs.getString("complemento"));
            u.setNumeroResidencia(rs.getInt("numeroResidencia"));

            String str = Double.toString(u.getClassificacao());

            if (u.getClassificacao() == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));
            }

            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosEmAndamentoObj(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao l \n"
                + "left join usuario u on l.idUsuario = u.idUsuario \n"
                + "where l.situacao = 'EM ANDAMENTO' and "
                + "l.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setClassificacao(rs.getDouble("classificacao"));
            u.setTelefone(rs.getString("telefone"));
            u.setEmail(rs.getString("email"));
            u.setEstado(rs.getString("estado"));
            u.setCidade(rs.getString("cidade"));
            u.setBairro(rs.getString("bairro"));
            u.setEndereco(rs.getString("endereco"));
            u.setComplemento(rs.getString("complemento"));
            u.setNumeroResidencia(rs.getInt("numeroResidencia"));

            String str = Double.toString(u.getClassificacao());

            if (u.getClassificacao() == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));
            }

            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getPedidosDevolvidosObj(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao l \n"
                + "left join usuario u on l.idUsuario = u.idUsuario \n"
                + "where l.situacao = 'FINALIZADO' and "
                + "l.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setClassificacao(rs.getDouble("classificacao"));
            u.setTelefone(rs.getString("telefone"));
            u.setEmail(rs.getString("email"));
            u.setEstado(rs.getString("estado"));
            u.setCidade(rs.getString("cidade"));
            u.setBairro(rs.getString("bairro"));
            u.setEndereco(rs.getString("endereco"));
            u.setComplemento(rs.getString("complemento"));
            u.setNumeroResidencia(rs.getInt("numeroResidencia"));

            String str = Double.toString(u.getClassificacao());

            if (u.getClassificacao() == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));
            }

            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getListaHistoricoObjeto(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao a \n"
                + "left join objeto i on a.idObjeto = i.idObjeto \n"
                + "left join usuario u on a.idUsuario = u.idUsuario \n"
                + "where a.situacao = 'FINALIZADO' and "
                + "a.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));
            a.setPreco(rs.getDouble("preco"));
            a.setCep(rs.getString("cep"));
            a.setEstadoPegada(rs.getString("estadoPegada"));
            a.setCidadePegada(rs.getString("cidadePegada"));
            a.setBairroPegada(rs.getString("bairroPegada"));
            a.setEnderecoPegada(rs.getString("enderecoPegada"));
            a.setNumeroResidencia(rs.getInt("numeroResidencia"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));

            a.setUsuario(u);
            a.setObjeto(i);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getListaPrecoTotObjeto(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select sum(l.preco) as soma from usuario u, objeto o, locacao l \n"
                + "where l.situacao = 'FINALIZADO' and "
                + "u.idUsuario = o.idUsuario and "
                + "o.idObjeto = l.idObjeto and "
                + "l.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setSoma(rs.getDouble("soma"));

            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getListaTotUsu(Usuario usuario) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select count(l.idLocacao) as total from locacao l  \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "where l.idUsuario = " + usuario.getIdUsuario() + " and \n"
                + " l.situacao = 'FINALIZADO'";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Usuario u = new Usuario();
            u.setTotal(rs.getInt("total"));

            Locação a = new Locação();

            a.setUsuario(u);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getListaTotObjt(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select count(l.idLocacao) as total from locacao l  \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "where l.idObjeto = " + objeto.getIdObjeto() + " and \n"
                + " l.situacao = 'FINALIZADO'";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Objeto o = new Objeto();
            o.setTotal(rs.getInt("total"));

            Locação a = new Locação();

            a.setObjeto(o);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getListaPeriodo(String dt1, String dt2, Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select * from locacao l\n"
                + "inner join objeto o on o.idObjeto = l.idObjeto\n"
                + "inner join usuario u on u.idUsuario = l.idUsuario\n"
                + "where\n"
                + "(l.dataInicial between ? and ?) and "
                + "l.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, dt1);
        stmt.setString(2, dt2);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Locação a = new Locação();
            a.setIdLocacao(rs.getInt("idLocacao"));
            a.setSituacao(rs.getString("situacao"));
            a.setPreco(rs.getDouble("preco"));
            a.setDataInicial(rs.getDate("dataInicial"));
            a.setDataFinal(rs.getDate("dataFinal"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setPreco(rs.getDouble("preco"));

            a.setUsuario(u);
            a.setObjeto(i);
            lista.add(a);
        }
        return lista;
    }

    public static List<Locação> getMediaAval(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = " select AVG(l.classificacaoItem) as media from locacao l \n"
                + "inner join objeto o on l.idObjeto = o.idObjeto \n"
                + "inner join usuario u on u.idUsuario = L.idUsuario \n"
                + "where o.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Objeto i = new Objeto();
            i.setMedia(rs.getDouble("media"));
            String str = Double.toString(i.getMedia());
            if (rs.getDouble("media") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));
            }

            Locação l = new Locação();
            l.setObjeto(i);
            lista.add(l);
        }
        return lista;
    }

    public static List<Locação> getListaOpini(Objeto objeto) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = "select l.classificacaoItem, l.opiniaoItem, u.nomeUsuario from locacao l \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "where l.idObjeto =  " + objeto.getIdObjeto() + " and \n"
                + "l.classificacaoItem IS NOT NULL ";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setNomeUsuario(rs.getString("nomeUsuario"));

            Locação l = new Locação();
            l.setClassificacaoItem(rs.getInt("classificacaoItem"));
            l.setOpiniaoItem(rs.getString("opiniaoItem"));

            l.setUsuario(u);
            lista.add(l);
        }
        return lista;
    }

    public static List<Locação> getListaOpiniUsuLocat(Usuario usuario) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = "select l.classificacaoLocatario, l.opiniaoLocatario, u.nomeUsuario from locacao l \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "where o.idUsuario =  " + usuario.getIdUsuario() + " and \n"
                + "l.classificacaoLocatario IS NOT NULL ";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setNomeUsuario(rs.getString("nomeUsuario"));

            Locação l = new Locação();
            l.setClassificacaoLocatario(rs.getInt("classificacaoLocatario"));
            l.setOpiniaoLocatario(rs.getString("opiniaoLocatario"));

            l.setUsuario(u);
            lista.add(l);
            System.out.println(l.getIdLocacao());
        }

        sql = "select AVG(l.classificacaoLocatario) as mediaLocat from locacao l  \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "WHERE o.idUsuario = " + usuario.getIdUsuario();

        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
            usuario.setMediaLocat(rs.getDouble("mediaLocat"));
            String str = Double.toString(usuario.getMediaLocat());
            if (rs.getDouble("mediaLocat") == 0.0) {
                usuario.setMensMedia("sem classificação");
                usuario.setMsglocat(usuario.getMensMedia());
            } else {
                usuario.setMsglocat(str.replace('.', ','));
            }
        }

        return lista;
    }

    public static List<Locação> getListaOpiniUsuLocad(Usuario usuario) throws SQLException {
        List<Locação> lista = new ArrayList<Locação>();
        Connection con = Conexao.getConexao();
        String sql = "select l.classificacaoLocador, l.opiniaoLocador, o.idUsuario from locacao l \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "left join objeto o on o.idObjeto = l.idObjeto \n"
                + "where u.idUsuario =  " + usuario.getIdUsuario() + " and \n"
                + "l.classificacaoLocador IS NOT NULL ";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));

            String sql2 = "select u.nomeUsuario from usuario u \n"
                    + "where u.idUsuario =  " + u.getIdUsuario();

            PreparedStatement stmt2 = con.prepareStatement(sql2);
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                u.setNomeUsuario(rs2.getString("nomeUsuario"));
            }

            Objeto o = new Objeto();
            o.setUsuario(u);

            Locação l = new Locação();
            l.setClassificacaoLocador(rs.getInt("classificacaoLocador"));
            l.setOpiniaoLocador(rs.getString("opiniaoLocador"));

            l.setObjeto(o);
            lista.add(l);
        }

        sql = "select AVG(l.classificacaoLocador) as mediaLocad from locacao l  \n"
                + "left join usuario u on u.idUsuario = l.idUsuario \n"
                + "WHERE u.idUsuario = " + usuario.getIdUsuario();

        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
            usuario.setMediaLocad(rs.getDouble("mediaLocad"));
            String str = Double.toString(usuario.getMediaLocad());
            if (rs.getDouble("mediaLocad") == 0.0) {
                usuario.setMensMedia("sem classificação");
                usuario.setMsglocad(usuario.getMensMedia());
            } else {
                usuario.setMsglocad(str.replace('.', ','));
            }
        }

        return lista;
    }

    public static void main(String[] args) {

        try {
            List<Locação> l = getLista();
            for (Locação est : l) {
                System.out.println("ID........:" + est.getIdLocacao());
                System.out.println("DATA INICIAL......:" + est.getDataInicial());
                System.out.println("DATA FINAL......:" + est.getDataFinal());
                System.out.println("Locatário......:" + est.getUsuario().getNomeUsuario());
                System.out.println("-------------------------------------");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}
