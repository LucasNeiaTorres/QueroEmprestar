package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Locação;
import modelo.Categoria;
import modelo.Objeto;
import modelo.Usuario;
import modelo.TipoPreco;
import util.Conexao;
import util.SessionContext;

public class ObjetoDAO {

    public static void inserir(Objeto objeto) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "insert into objeto \n"
                + "(nome, descricaoItem, marca, modelo, preco, cep, estado, cidade, bairro, endereco, numeroResidencia, complemento, imagem,  idTipoPreco, idCategoria, idUsuario, condicao)"
                + "values\n"
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, objeto.getNome());
        stmt.setString(2, objeto.getDescricaoItem());
        stmt.setString(3, objeto.getMarca());
        stmt.setString(4, objeto.getModelo());
        stmt.setDouble(5, objeto.getPreco());
        stmt.setString(6, objeto.getCep());
        stmt.setString(7, objeto.getEstado());
        stmt.setString(8, objeto.getCidade());
        stmt.setString(9, objeto.getBairro());
        stmt.setString(10, objeto.getEndereco());
        stmt.setInt(11, objeto.getNumeroResidencia());
        stmt.setString(12, objeto.getComplemento());
        stmt.setString(13, objeto.getImagem());
        stmt.setInt(14, objeto.getTipoPreco().getIdTipoPreco());
        stmt.setInt(15, objeto.getCategoria().getIdCategoria());
        stmt.setInt(16, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.setString(17, objeto.getCondicao());
        stmt.execute();

        stmt.close();
        con.close();
    }

    public static void alterar(Objeto objeto) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update objeto set \n"
                + "nome=?, marca=?, modelo=?, descricaoItem=?, preco=?, cep=?, estado=?, cidade=?, "
                + "bairro=?, endereco=?, numeroResidencia=?, complemento=?, imagem=?, idTipoPreco=?, "
                + "idCategoria=?, idUsuario=?\n"
                + "WHERE idObjeto=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, objeto.getNome());
        stmt.setString(2, objeto.getMarca());
        stmt.setString(3, objeto.getModelo());
        stmt.setString(4, objeto.getDescricaoItem());
        stmt.setDouble(5, objeto.getPreco());
        stmt.setString(6, objeto.getCep());
        stmt.setString(7, objeto.getEstado());
        stmt.setString(8, objeto.getCidade());
        stmt.setString(9, objeto.getBairro());
        stmt.setString(10, objeto.getEndereco());
        stmt.setInt(11, objeto.getNumeroResidencia());
        stmt.setString(12, objeto.getComplemento());
        stmt.setString(13, objeto.getImagem());
        stmt.setInt(14, objeto.getTipoPreco().getIdTipoPreco());
        stmt.setInt(15, objeto.getCategoria().getIdCategoria());
        stmt.setInt(16, SessionContext.getInstance().getUsuarioLogado().getIdUsuario());
        stmt.setInt(17, objeto.getIdObjeto());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void inativar(Objeto objeto) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "update objeto set \n"
                + "status = 'INATIVO'"
                + "WHERE idObjeto=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, objeto.getIdObjeto());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static void excluir(Objeto objeto) throws SQLException {

        Connection con = Conexao.getConexao();
        String sql = "delete from objeto WHERE idObjeto=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, objeto.getIdObjeto());
        stmt.execute();
        stmt.close();
        con.close();
    }

    public static List<Objeto> getLista(Usuario usuarioLogado) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql;
        if (usuarioLogado == null) {
            sql = "select * from objeto i \n"
                    + "left join categoria c on i.idCategoria = c.idCategoria \n"
                    + "left join tipoPreco t on i.idTipoPreco = t.idTipoPreco\n"
                    + "left join usuario u on i.idUsuario = u.idUsuario \n"
                    + "WHERE i.status = 'ATIVO' \n"
                    + "order by i.nome";
        } else {
            sql = "select * from objeto i \n"
                    + "left join categoria c on i.idCategoria = c.idCategoria \n"
                    + "left join tipoPreco t on i.idTipoPreco = t.idTipoPreco\n"
                    + "left join usuario u on i.idUsuario = u.idUsuario \n"
                    + "where i.idUsuario != " + usuarioLogado.getIdUsuario() + " AND \n"
                    + "i.status = 'ATIVO' \n"
                    + "order by i.nome";
        }
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setCep(rs.getString("cep"));
            u.setClassificacao(rs.getDouble("u.classificacao"));
            u.setCpf(rs.getString("cpf"));
            u.setTelefone(rs.getString("telefone"));
            u.setEmail(rs.getString("email"));
            u.setEstado(rs.getString("u.estado"));
            u.setCidade(rs.getString("u.cidade"));
            u.setBairro(rs.getString("u.bairro"));
            u.setEndereco(rs.getString("u.endereco"));
            u.setNumeroResidencia(rs.getInt("u.numeroResidencia"));
            u.setComplemento(rs.getString("u.complemento"));

            String str = Double.toString(u.getClassificacao());

            if (rs.getDouble("u.classificacao") == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));

            }

            Categoria c = new Categoria();
            c.setIdCategoria(rs.getInt("idCategoria"));
            c.setDescricaoCategoria(rs.getString("descricaoCategoria"));

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
            i.setCondicao(rs.getString("condicao"));
            i.setFormaEntrega(rs.getString("formaEntrega"));
            i.setImagem(rs.getString("imagem"));
            i.setClassificacao(rs.getDouble("classificacao"));

            str = Double.toString(i.getClassificacao());

            if (rs.getDouble("classificacao") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            i.setTipoPreco(t);
            i.setUsuario(u);
            i.setCategoria(c);
            lista.add(i);
        }
        return lista;
    }

    public static List<Objeto> getListaUsuario(Usuario usuarioLogado) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql = " select * from objeto i \n"
                + "left join categoria c on i.idCategoria = c.idCategoria \n"
                + "left join tipoPreco t on i.idTipoPreco = t.idTipoPreco \n"
                + "where idUsuario =  " + usuarioLogado.getIdUsuario();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));

            Categoria c = new Categoria();
            c.setIdCategoria(rs.getInt("idCategoria"));
            c.setDescricaoCategoria(rs.getString("descricaoCategoria"));

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

            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            i.setTipoPreco(t);
            i.setUsuario(u);
            i.setCategoria(c);
            lista.add(i);
        }
        return lista;
    }

    public static List<Objeto> getListaUsuSele(Usuario usuario) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql = " select * from objeto i \n"
                + "left join categoria c on i.idCategoria = c.idCategoria \n"
                + "left join tipoPreco t on i.idTipoPreco = t.idTipoPreco \n"
                + "left join usuario u on i.idUsuario = u.idUsuario \n"
                + "where i.idUsuario =  " + usuario.getIdUsuario()
                + " order by i.nome";

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));

            Usuario u = new Usuario();
            u = usuario;
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("u.classificacao"));

            String str = Double.toString(u.getClassificacao());

            if (u.getClassificacao() == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));
            }

            Categoria c = new Categoria();
            c.setIdCategoria(rs.getInt("idCategoria"));
            c.setDescricaoCategoria(rs.getString("descricaoCategoria"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setCondicao(rs.getString("condicao"));
            i.setFormaEntrega(rs.getString("formaEntrega"));
            i.setPreco(rs.getDouble("preco"));
            i.setCep(rs.getString("cep"));
            i.setEstado(rs.getString("estado"));
            i.setCidade(rs.getString("cidade"));
            i.setBairro(rs.getString("bairro"));
            i.setEndereco(rs.getString("endereco"));
            i.setNumeroResidencia(rs.getInt("numeroResidencia"));
            i.setComplemento(rs.getString("complemento"));
            i.setImagem(rs.getString("imagem"));
            i.setClassificacao(rs.getDouble("i.classificacao"));

            str = Double.toString(i.getClassificacao());

            if (i.getClassificacao() == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            i.setTipoPreco(t);
            i.setUsuario(u);
            i.setCategoria(c);
            lista.add(i);
        }
        return lista;
    }

    public static List<Objeto> getListaPesquisaCep(String pesq, Usuario usuarioLogado, Categoria categoria, String cep) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql;
        if (categoria.getIdCategoria() != 0) {

            if (usuarioLogado == null) {
                sql = " select * from objeto o\n"
                        + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                        + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                        + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                        + "where nome \n"
                        + "like ? \n"
                        + "and c.idCategoria = " + categoria.getIdCategoria() + " and \n"
                        + "o.cep = " + cep;

            } else {
                sql = " select * from objeto o\n"
                        + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                        + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                        + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                        + "where nome \n"
                        + "like ? AND \n"
                        + "c.idCategoria = " + categoria.getIdCategoria() + " and \n"
                        + "o.cep = " + cep + " and \n"
                        + "o.idUsuario != " + usuarioLogado.getIdUsuario();
            }

        } else {
            if (usuarioLogado == null) {
                sql = " select * from objeto o\n"
                        + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                        + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                        + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                        + "where nome \n"
                        + "like ? and \n"
                        + "o.cep = " + cep;

            } else {
                sql = " select * from objeto o\n"
                        + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                        + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                        + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                        + "where nome \n"
                        + "like ? AND \n"
                        + "o.cep = " + cep + " and \n"
                        + "o.idUsuario != " + usuarioLogado.getIdUsuario();
            }
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, "%" + pesq + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("u.classificacao"));
            u.setEstado(rs.getString("u.estado"));
            u.setCidade(rs.getString("u.cidade"));
            u.setBairro(rs.getString("u.bairro"));
            u.setEndereco(rs.getString("u.endereco"));
            u.setNumeroResidencia(rs.getInt("u.numeroResidencia"));
            u.setComplemento(rs.getString("u.complemento"));

            String str = Double.toString(u.getClassificacao());

            if (rs.getDouble("u.classificacao") == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));

            }

            Categoria c = new Categoria();
            c.setIdCategoria(rs.getInt("idCategoria"));
            c.setDescricaoCategoria(rs.getString("descricaoCategoria"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setCondicao(rs.getString("condicao"));
            i.setFormaEntrega(rs.getString("formaEntrega"));
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

            str = Double.toString(i.getClassificacao());

            if (rs.getDouble("classificacao") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            i.setTipoPreco(t);
            i.setUsuario(u);
            i.setCategoria(c);
            lista.add(i);
        }
        return lista;
    }

    public static List<Objeto> getListaPesquisaCat(String pesq, Usuario usuarioLogado, Categoria categoria) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql;
        if (usuarioLogado == null) {
            sql = " select * from objeto o\n"
                    + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                    + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                    + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                    + "where nome \n"
                    + "like ? \n"
                    + "and c.idCategoria = " + categoria.getIdCategoria();

        } else {
            sql = " select * from objeto o\n"
                    + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                    + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                    + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                    + "where nome \n"
                    + "like ? AND \n"
                    + "c.idCategoria = " + categoria.getIdCategoria() + " and \n"
                    + "o.idUsuario != " + usuarioLogado.getIdUsuario();
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, "%" + pesq + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("u.classificacao"));
            u.setEstado(rs.getString("u.estado"));
            u.setCidade(rs.getString("u.cidade"));
            u.setBairro(rs.getString("u.bairro"));
            u.setEndereco(rs.getString("u.endereco"));
            u.setNumeroResidencia(rs.getInt("u.numeroResidencia"));
            u.setComplemento(rs.getString("u.complemento"));

            String str = Double.toString(u.getClassificacao());

            if (rs.getDouble("u.classificacao") == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));

            }

            Categoria c = new Categoria();
            c.setIdCategoria(rs.getInt("idCategoria"));
            c.setDescricaoCategoria(rs.getString("descricaoCategoria"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setCondicao(rs.getString("condicao"));
            i.setFormaEntrega(rs.getString("formaEntrega"));
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

            str = Double.toString(i.getClassificacao());

            if (rs.getDouble("classificacao") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            i.setTipoPreco(t);
            i.setUsuario(u);
            i.setCategoria(c);
            lista.add(i);
        }
        return lista;
    }

    public static List<Objeto> getListaPesquisa(String pesq, Usuario usuarioLogado) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql;
        if (usuarioLogado == null) {
            sql = " select * from objeto o\n"
                    + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                    + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                    + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                    + "where nome \n"
                    + "like ? ";

        } else {
            sql = " select * from objeto o\n"
                    + "inner join tipopreco t on t.idTipoPreco = o.idTipoPreco\n"
                    + "inner join categoria c on c.idCategoria = o.idCategoria\n"
                    + "inner join usuario u on u.idUsuario = o.idUsuario\n"
                    + "where nome \n"
                    + "like ? AND \n"
                    + "o.idUsuario != " + usuarioLogado.getIdUsuario();
        }

        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, "%" + pesq + "%");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TipoPreco t = new TipoPreco();
            t.setIdTipoPreco(rs.getInt("idTipoPreco"));
            t.setDescricaoTipoPreco(rs.getString("descricaoTipoPreco"));

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNomeUsuario(rs.getString("nomeUsuario"));
            u.setTelefone(rs.getString("telefone"));
            u.setClassificacao(rs.getDouble("u.classificacao"));
            u.setEstado(rs.getString("u.estado"));
            u.setCidade(rs.getString("u.cidade"));
            u.setBairro(rs.getString("u.bairro"));
            u.setEndereco(rs.getString("u.endereco"));
            u.setNumeroResidencia(rs.getInt("u.numeroResidencia"));
            u.setComplemento(rs.getString("u.complemento"));

            String str = Double.toString(u.getClassificacao());

            if (rs.getDouble("u.classificacao") == 0.0) {
                u.setMensMedia("sem classificação");
                u.setMsg(u.getMensMedia());
            } else {
                u.setMsg(str.replace('.', ','));

            }

            Categoria c = new Categoria();
            c.setIdCategoria(rs.getInt("idCategoria"));
            c.setDescricaoCategoria(rs.getString("descricaoCategoria"));

            Objeto i = new Objeto();
            i.setIdObjeto(rs.getInt("idObjeto"));
            i.setNome(rs.getString("nome"));
            i.setDescricaoItem(rs.getString("descricaoItem"));
            i.setMarca(rs.getString("marca"));
            i.setModelo(rs.getString("modelo"));
            i.setCondicao(rs.getString("condicao"));
            i.setFormaEntrega(rs.getString("formaEntrega"));
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

            str = Double.toString(i.getClassificacao());

            if (rs.getDouble("classificacao") == 0.0) {
                i.setMensMedia("sem classificação");
                i.setMsg(i.getMensMedia());
            } else {
                i.setMsg(str.replace('.', ','));

            }

            i.setTipoPreco(t);
            i.setUsuario(u);
            i.setCategoria(c);
            lista.add(i);
        }
        return lista;
    }

    public static List<Objeto> getMediaAval(Objeto objeto) throws SQLException {
        List<Objeto> lista = new ArrayList<Objeto>();
        Connection con = Conexao.getConexao();
        String sql = " select AVG(l.classificacaoItem) as media from locacao l \n"
                + "inner join objeto o on l.idObjeto = o.idObjeto \n"
                + "inner join usuario u on u.idUsuario = L.idUsuario \n"
                + "where o.idObjeto = " + objeto.getIdObjeto();

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {

            Objeto o = new Objeto();
            o.setMedia(rs.getDouble("media"));
            String str = Double.toString(o.getMedia());

            if (rs.getDouble("media") == 0.0) {
                o.setMensMedia("sem classificação");
                o.setMsg(o.getMensMedia());
            } else {
                o.setMsg(str.replace('.', ','));

            }
            lista.add(o);
        }
        return lista;
    }

    public static void main(String[] args) {

        List<Objeto> l = getLista();
        for (Objeto est : l) {
            System.out.println("ID........:" + est.getIdObjeto());
            System.out.println("NOME......:" + est.getNome());
            System.out.println("CATEGORIA.........." + est.getCategoria().getDescricaoCategoria());
            System.out.println("-------------------------------------");
        }

    }

    private static List<Objeto> getLista() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
