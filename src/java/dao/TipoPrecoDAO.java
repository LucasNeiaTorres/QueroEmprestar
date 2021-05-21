package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.TipoPreco;
import util.Conexao;

public class TipoPrecoDAO {
    public static void inserir(TipoPreco tipoPreco) throws SQLException{
        
        Connection con = Conexao.getConexao();
        String sql = "insert into tipoPreco \n" +
        "(descricaoTipoPreco)"
        +"values\n" 
        +"(?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, tipoPreco.getDescricaoTipoPreco());
        stmt.execute();
        stmt.close();
        con.close();
    }
    public static void alterar(TipoPreco tipoPreco) throws SQLException{
        Connection con = Conexao.getConexao();
        String sql = "update tipoPreco set \n"
                +"descricaoTipoPreco=? WHERE idTipoPreco=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, tipoPreco.getDescricaoTipoPreco());
        stmt.setInt(2, tipoPreco.getIdTipoPreco());
        stmt.execute();
        stmt.close();
        con.close();
    }
    public static void excluir(TipoPreco tipoPreco) throws SQLException{
        
        Connection con = Conexao.getConexao();
        String sql = "delete from tipoPreco WHERE idTipoPreco=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, tipoPreco.getIdTipoPreco());
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static List<TipoPreco> getLista() throws SQLException{
            List<TipoPreco> lista = new ArrayList<TipoPreco>();
            Connection con = Conexao.getConexao();
            String sql = "select * from tipoPreco t \n"
                    + "ORDER BY t.idTipoPreco";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                TipoPreco t = new TipoPreco();
                t.setIdTipoPreco(rs.getInt("idTipoPreco"));
                t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));
                lista.add(t);           
            }
            return lista;
    }
}
