package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;
import util.Conexao;


public class UsuarioDAO {
    
    public static void inserir(Usuario usuario) throws SQLException{
            
        Connection con = Conexao.getConexao();
        String sql = "insert into usuario \n" +
        "(nomeUsuario, email, telefone, cep, bairro, endereco, numeroResidencia, cpf, complemento, estado, cidade, senha)"
        +"values\n" 
        +"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, md5(?))";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, usuario.getNomeUsuario());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getTelefone());
        stmt.setString(4, usuario.getCep());
        stmt.setString(5, usuario.getBairro());
        stmt.setString(6, usuario.getEndereco());
        stmt.setInt(7, usuario. getNumeroResidencia());
        stmt.setString(8, usuario.getCpf());
        stmt.setString(9, usuario.getComplemento());
        stmt.setString(10, usuario.getEstado());
        stmt.setString(11, usuario.getCidade());
        stmt.setString(12, usuario.getSenha());
        stmt.execute();
        stmt.close();
        con.close();
    }
    public static void alterar(Usuario usuario) throws SQLException{
        
        Connection con = Conexao.getConexao();
        String sql = "update usuario set \n"
        +"nomeUsuario=?, email=?, telefone=?, cep=?, bairro=?, endereco=?, numeroResidencia=?, cpf=?, complemento=?, estado=?, cidade=? \n"
        +"WHERE idUsuario=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, usuario.getNomeUsuario());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getTelefone());
        stmt.setString(4, usuario.getCep());
        stmt.setString(5, usuario.getBairro());
        stmt.setString(6, usuario.getEndereco());
        stmt.setInt(7, usuario.getNumeroResidencia());
        stmt.setString(8, usuario.getCpf());
        stmt.setString(9, usuario.getComplemento());
        stmt.setString(10, usuario.getEstado());
        stmt.setString(11, usuario.getCidade());
        stmt.setInt(12, usuario.getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static void inativar(Usuario usuario) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update usuario set \n"
                + "status = 'INATIVO'"
                + "WHERE idUsuario=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, usuario.getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static void excluir(Usuario usuario) throws SQLException{
        
        Connection con = Conexao.getConexao();
        String sql = "delete from usuario WHERE idUsuario=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, usuario.getIdUsuario());
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static List<Usuario> getLista() throws SQLException{
            List<Usuario> lista = new ArrayList<Usuario>();
            Connection con = Conexao.getConexao();
            String sql = " select * from usuario u \n"
            +"order by u.nomeUsuario";
            
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNomeUsuario(rs.getString("NomeUsuario"));
                u.setBairro(rs.getString("bairro"));
                u.setCep(rs.getString("cep"));
                u.setComplemento(rs.getString("complemento"));
                u.setCpf(rs.getString("cpf"));
                u.setEmail(rs.getString("email"));
                u.setEndereco(rs.getString("endereco"));
                u.setNumeroResidencia(rs.getInt("numeroResidencia"));
                u.setTelefone(rs.getString("telefone"));
                u.setEstado("estado");
                u.setCidade("cidade");
                lista.add(u);    
            }
            return lista;
    }
    public static Usuario getLogin(Usuario u) throws SQLException{
            Usuario usuario = null;
            Connection con = Conexao.getConexao();
            String sql = " select * from usuario u\n" +
            "where \n" +
            "u.email = ? and\n" +
            "u.status = 'ATIVO' and \n" +
            "u.senha = md5(?)";
           
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, u.getEmail());
            stmt.setString(2, u.getSenha());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNomeUsuario(rs.getString("NomeUsuario"));
                usuario.setBairro(rs.getString("bairro"));
                usuario.setCep(rs.getString("cep"));
                usuario.setComplemento(rs.getString("complemento"));
                usuario.setCpf(rs.getString("cpf"));
                usuario.setEmail(rs.getString("email"));
                usuario.setEndereco(rs.getString("endereco"));
                usuario.setNumeroResidencia(rs.getInt("numeroResidencia"));
                usuario.setTelefone(rs.getString("telefone")); 
                usuario.setEstado(rs.getString("estado"));
                usuario.setCidade(rs.getString("cidade"));
                usuario.setAdm(rs.getString("adm"));
            }
            return usuario;
    }
    
    public static void main(String[] args) {
  
        try{
            List<Usuario> l = getLista();
            for (Usuario est : l) {
                System.out.println("ID........:"+est.getIdUsuario());
                System.out.println("NOME......:"+est.getNomeUsuario());
                System.out.println("-------------------------------------");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        
    }
   
}
