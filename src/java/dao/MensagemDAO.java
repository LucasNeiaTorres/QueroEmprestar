package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Objeto;
import modelo.Mensagem;
import modelo.Usuario;
import util.Conexao;
import util.SessionContext;

public class MensagemDAO {

    public static void inserirPergunta(Mensagem mensagem) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "insert into mensagem \n"
                + "(descricaoMensagem, dataHora, idObjeto, idUsuario)"
                + "values\n"
                + "(?, CURRENT_TIMESTAMP(), ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, mensagem.getDescricaoMensagem());
        stmt.setInt(2, mensagem.getObjeto().getIdObjeto());
        stmt.setInt(3, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void inserirResposta(Mensagem mensagem) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "insert into mensagem \n"
                + "(descricaoMensagem, dataHora, idPerguntaPai, idObjeto, idUsuario)"
                + "values\n"
                + "(?, CURRENT_TIMESTAMP(), ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, mensagem.getDescricaoMensagem());
        stmt.setInt(2, mensagem.getPerguntaPai().getIdMensagem());
        stmt.setInt(3, mensagem.getObjeto().getIdObjeto());
        stmt.setInt(4, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
        System.out.println(mensagem.getObjeto().getIdObjeto());
        System.out.println(mensagem.getPerguntaPai().getIdMensagem());
        System.out.println(SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        System.out.println(mensagem.getDescricaoMensagem());
    }

    public static void alterar(Mensagem mensagem) throws SQLException {
        Connection con = Conexao.getConexao();
        String sql = "update mensagem set \n"
                + "descricaoMensagem=?, dataHora=?, idPerguntaPai=?, idObjeto=?, idUsuario=? WHERE idMensagem=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, mensagem.getDescricaoMensagem());
        stmt.setDate(2, new Date(mensagem.getDataHora().getTime()));
        stmt.setInt(3, mensagem.getPerguntaPai().getIdMensagem());
        stmt.setInt(4, mensagem.getObjeto().getIdObjeto());
        stmt.setInt(5, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.setInt(6, mensagem.getIdMensagem());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void excluir(Mensagem mensagem) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "delete from mensagem WHERE idMensagem=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, mensagem.getIdMensagem());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static List<Mensagem> getLista() throws SQLException {
        List<Mensagem> lista = new ArrayList<Mensagem>();
        Connection con = Conexao.getConexao();
        String sql = "select * from mensagem p \n"
                + "ORDER BY p.idMensagem";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));

            Mensagem p = new Mensagem();
            p.setIdMensagem(rs.getInt("idMensagem"));
            p.setDescricaoMensagem(rs.getString("descricaoMensagem"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));

            Mensagem m = new Mensagem();
            m.setIdMensagem(rs.getInt("idMensagem"));
            m.setDescricaoMensagem(rs.getString("descricaoMensagem"));
            m.setDataHora(rs.getDate("dataHora"));
            m.setUsuario(u);
            m.setPerguntaPai(p);
            m.setObjeto(i);
            lista.add(m);
        }
        return lista;
    }

    public static List<Mensagem> getListaObjetoRespostas(Mensagem p) throws SQLException {
        List<Mensagem> lista = new ArrayList<Mensagem>();
        Connection con = Conexao.getConexao();
        String sql = "select * from mensagem m \n"
                + "left join objeto i on m.idObjeto = i.idObjeto \n"
                + "left join mensagem p on p.idMensagem = m.idPerguntaPai \n"
                + "where m.idPerguntaPai = ? ";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, p.getIdMensagem());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));

            Mensagem m = new Mensagem();
            m.setIdMensagem(rs.getInt("idMensagem"));
            m.setDescricaoMensagem(rs.getString("descricaoMensagem"));
            m.setDataHora(rs.getDate("dataHora"));
            m.setDescricaoResp(rs.getString("descricaoMensagem"));

            p.setDescricaoResp(m.getDescricaoResp());

            m.setUsuario(u);
            m.setPerguntaPai(p);
            m.setObjeto(i);
            lista.add(m);
        }
        return lista;
    }

    public static List<Mensagem> getListaObjetoPerguntas(Objeto objeto) throws SQLException {
        List<Mensagem> lista = new ArrayList<Mensagem>();
        Connection con = Conexao.getConexao();
        String sql = "select * from mensagem p \n"
                + "left join objeto i on p.idObjeto = i.idObjeto \n"
                + "where p.idPerguntaPai IS NULL and "
                + "p.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));

            Mensagem p = new Mensagem();
            p.setIdMensagem(rs.getInt("idMensagem"));
            p.setDescricaoMensagem(rs.getString("descricaoMensagem"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));

            getListaObjetoRespostas(p);

            p.setUsuario(u);
            p.setObjeto(i);

            if (p.getDescricaoResp() == null) {
                p.setDescricaoResp("*Não há resposta ainda*");
            }

            lista.add(p);
        }

        return lista;
    }

    public static List<Mensagem> getListaObjeto(Objeto objeto) throws SQLException {
        List<Mensagem> lista = new ArrayList<Mensagem>();
        Connection con = Conexao.getConexao();
        String sql = "select * from mensagem m \n"
                + "left join objeto i on m.idObjeto = i.idObjeto \n"
                + "where m.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));

            Mensagem p = new Mensagem();
            p.setIdMensagem(rs.getInt("idMensagem"));
            p.setDescricaoMensagem(rs.getString("descricaoMensagem"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));

            Mensagem m = new Mensagem();
            m.setIdMensagem(rs.getInt("idMensagem"));
            m.setDescricaoMensagem(rs.getString("descricaoMensagem"));
            m.setDataHora(rs.getDate("dataHora"));

            m.setUsuario(u);
            m.setPerguntaPai(p);
            m.setObjeto(i);
            lista.add(m);
        }
        return lista;
    }
}
