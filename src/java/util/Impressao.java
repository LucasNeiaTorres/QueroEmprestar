package util;

import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author HÃ©ber Morais 
 * @since 16/12/2015
 */
public class Impressao {

    public void gerarRelatorio(HashMap<String, Object> lista, String caminhoRel, String nome) {
        try {
            String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath(caminhoRel);// parametro contendo o caminho para o relatÃ³rio
            JasperPrint jasperPrint = JasperFillManager.fillReport(caminho, lista, Conexao.getConexao());//passando parÃ¢metro para o relatÃ³rio inclusive conexÃ£o
            System.out.println("Caminho: "+caminho);
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + nome + ".pdf");//Gerando PDF como nome  
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();//gerando resposta de saÃ­da
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);//exportando em PDF
            FacesContext.getCurrentInstance().responseComplete();//finalizando relatÃ³rio
        } catch (Exception erro) {
            erro.printStackTrace();// ExceÃ§Ã£o
            JOptionPane.showMessageDialog(null, "nao abriu relatorio: " + erro);
        }
    }

}
