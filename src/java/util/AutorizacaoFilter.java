/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Usuario;

@WebFilter("*.xhtml")
public class AutorizacaoFilter implements Filter {

    private static final String[][] DIREITO_ACESSO = {
        {"ATIVO",
            "/faces/login.xhtml",
            "/faces/template/conteudo.xhtml",
            "/faces/login_1.xhtml",
            "/faces/meuPerfil.xhtml",
            "/faces/meusPedidos.xhtml",
            "/faces/PáginasEmComum/paginaInicial.xhtml",
            "/faces/paginaInicial.xhtml",
            "/faces/paginaInicial_adm.xhtml",
            "/faces/itemSelecionado.xhtml",
            "/faces/administrador.xhtml",
            "/faces/manutencaoCategorias.xhtml",
            "/faces/cadastroCategorias.xhtml",
            "/faces/manutencaoAluguel.xhtml",
            "/faces/cadastroAluguel.xhtml",
            "/faces/cadastroAluguel_1.xhtml",
            "/faces/cadastroItens.xhtml",
            "/faces/cadastroItens_1.xhtml",
            "/faces/cadastroObjeto.xhtml",
            "/faces/manutencaoItens.xhtml",
            "/faces/calendarioItem.xhtml",
            "/faces/cadastroUsuarios.xhtml",
            "/faces/manutencaoUsuarios.xhtml",
            "/faces/cadastroMensagens.xhtml",
            "/faces/manutencaoMensagens.xhtml",
            "/faces/cadastroTipoPrecos.xhtml",
            "/faces/pedidosItem.xhtml",
            "/faces/relatorios.xhtml",
            "/faces/manutencaoItens_1.xhtml",
            "/faces/manutencaoTipoPrecos.xhtml",
            "/faces/usuarioSelecionado.xhtml",
            "/faces/infoPedidoItem.xhtml",
            "/faces/infoPedido.xhtml",},
        {"INATIVO",
            "/faces/login.xhtml",
            "/faces/meuPerfil.xhtml",
            "/faces/meusPedidos.xhtml",
            "/faces/PáginasEmComum/paginaInicial.xhtml",
            "/faces/paginaInicial.xhtml",
            "/faces/itemSelecionado.xhtml",
            "/faces/itemSelecionado_1.xhtml",
            "/faces/manutencaoAluguel.xhtml",
            "/faces/cadastroAluguel.xhtml",
            "/faces/cadastroAluguel_1.xhtml",
            "/faces/cadastroItens.xhtml",
            "/faces/cadastroItens_1.xhtml",
            "/faces/cadastroObjeto.xhtml",
            "/faces/manutencaoItens.xhtml",
            "/faces/calendarioItem.xhtml",
            "/faces/cadastroUsuarios.xhtml",
            "/faces/cadastroMensagens.xhtml",
            "/faces/pedidosItem.xhtml",
            "/faces/infoPedido.xhtml",
            "/faces/relatorios.xhtml",
            "/faces/usuarioSelecionado.xhtml",
            "/faces/infoPedidoItem.xhtml",
            "/faces/manutencaoMensagens.xhtml",},};

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        Usuario user = null;

        HttpSession sess = ((HttpServletRequest) req).getSession();

        if (sess != null) {
            user = (Usuario) sess.getAttribute("usuario");

        }

        if ((user == null)
                && !request.getRequestURI().endsWith("/faces/login.xhtml")
                && !request.getRequestURI().endsWith("/faces/cadastroUsuarios.xhtml")
                && !request.getRequestURI().endsWith("/faces/paginaInicialSemLogin.xhtml")
                && !request.getRequestURI().endsWith("/faces/itemSelecionado.xhtml")
                && !request.getRequestURI().endsWith("/faces/usuarioSelecionado.xhtml")
                && !request.getRequestURI().endsWith("/faces/index.xhtml")
                && !request.getRequestURI().contains("/javax.faces.resource/")) {
            response.sendRedirect(request.getContextPath() + "/faces/login.xhtml");
        } else {
            try {
                boolean foi = false;
                if (user.getAdm().equals("ATIVO")) {
                    for (int i = 1; i < DIREITO_ACESSO[0].length; i++) {
                        if (request.getRequestURI().endsWith(DIREITO_ACESSO[0][i])) {
                            chain.doFilter(req, res);
                            foi = false;
                            break;
                        } else {
                            foi = true;
                        }
                    }
                    if (foi) {
                        response.sendRedirect(request.getContextPath() + "/faces/manutencaoItens.xhtml");
                    }
                } else if (user.getAdm().equals("INATIVO")) {
                    for (int i = 1; i < DIREITO_ACESSO[1].length; i++) {
                        if (request.getRequestURI().endsWith(DIREITO_ACESSO[1][i])) {
                            chain.doFilter(req, res);
                            foi = false;
                            break;
                        } else {
                            foi = true;
                        }
                    }
                    if (foi) {
                        response.sendRedirect(request.getContextPath() + "/faces/manutencaoItens.xhtml");
                    }

                }
            } catch (NullPointerException e) {
                chain.doFilter(req, res);
            }

        }

    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
