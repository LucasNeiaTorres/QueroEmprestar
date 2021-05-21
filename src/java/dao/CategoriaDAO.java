package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;
import util.Conexao;

public class CategoriaDAO {
    public static void inserir(Categoria categoria) throws SQLException{
        
        Connection con = Conexao.getConexao();
        String sql = "insert into categoria \n" +
        "(descricaoCategoria)"
        +"values\n" 
        +"(?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, categoria.getDescricaoCategoria());
        stmt.execute();
        stmt.close();
        con.close();
    }
    public static void alterar(Categoria categoria) throws SQLException{
        Connection con = Conexao.getConexao();
        String sql = "update categoria set \n"
                +"descricaoCategoria=? WHERE idCategoria=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, categoria.getDescricaoCategoria());
        stmt.setInt(2, categoria.getIdCategoria());
        stmt.execute();
        stmt.close();
        con.close();
    }
    public static void excluir(Categoria categoria) throws SQLException{
        
        Connection con = Conexao.getConexao();
        String sql = "delete from categoria WHERE idCategoria=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, categoria.getIdCategoria());
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static List<Categoria> getLista() throws SQLException{
            List<Categoria> lista = new ArrayList<Categoria>();
            Connection con = Conexao.getConexao();
            String sql = "select * from categoria c \n"
                    + "ORDER BY c.descricaoCategoria";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("idCategoria"));
                c.setDescricaoCategoria(rs.getString("descricaoCategoria"));
                lista.add(c);           
            }
            return lista;
    }
}
