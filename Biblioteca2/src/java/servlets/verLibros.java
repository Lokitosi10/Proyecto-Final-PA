/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import Entidad.Libro;
import Entidad.Usuario;
import controladores.LibroJpaController;
import controladores.UsuarioJpaController;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author andre
 */
@WebServlet(name = "verLibros", urlPatterns = {"/verLibros"})
public class verLibros extends HttpServlet {

    private EntityManager em;
    private Libro c;
    private Usuario usr;
    @Resource
    private javax.transaction.UserTransaction utx;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        //Conexion:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BibliotecaPU");
        em = emf.createEntityManager();
        LibroJpaController lbr = new LibroJpaController(utx, emf);
        List lLibro = lbr.findLibroEntities();
        HttpSession misession = (HttpSession) request.getSession();
        usr = (Usuario) misession.getAttribute("usuario");
        //tabla
        String libros, tabla = "";
        Iterator iter = lLibro.iterator();
        while (iter.hasNext()) {
            libros = (iter.next().toString());
            c = lbr.findLibro(Integer.parseInt(libros));
            tabla += "    <tr>\n"
                    + "      <td>" + c.getId() + "</td>\n"
                    + "      <td>" + c.getLibTitulo() + "</td>\n"
                    + "      <td>" + c.getLibPrecio() + "</td>\n"
                    + "      <td>" + c.getLibGenero() + "</td>\n"
                    + "    </tr>\n";
        }

        //condicionales usuario
        String pagina;
        if (usr.getRol() == 1) {
            pagina = "./bootstrap/adminPage.html";
        } else {
            pagina = "./bootstrap/userPage.html";
        }
        //Peticion
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Libros</title>");
            out.println("<link rel=\"stylesheet\" href=\"./bootstrap/css/bootstrap.min.css\">\n"
                    + "  <link rel=\"stylesheet\" href=\"./bootstrap/css/styles.css\">\n"
                    + "  <link href=\"https://fonts.googleapis.com/css2?family=Mukta:wght@200;300;400;500;600;700&display=swap\"\n"
                    + "    rel=\"stylesheet\">\n"
                    + "  <link href=\"https://fonts.googleapis.com/css2?family=Anton&display=swap\" rel=\"stylesheet\" type=\"text/css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<header>\n"
                    + "    <nav id=\"header-nav\" class=\"navbar navbar-default\">\n"
                    + "      <div class=\"container\">\n"
                    + "        <div class=\"navbar-header\">\n"
                    + "          <a href=\"" + pagina + "\" class=\"pull-left visible-md visible-lg\">\n"
                    + "            <div id=\"logo-img\"></div>\n"
                    + "          </a>\n"
                    + "\n"
                    + "          <div class=\"navbar-brand\">\n"
                    + "            <a href=\"" + pagina + "\">\n"
                    + "              <h1>BiblioBogota</h1>\n"
                    + "            </a>\n"
                    + "            <p>\n"
                    + "              <span class=\"glyphicon glyphicon-user\"></span>\n"
                    + "              <span>Red de Bibliotecas publicas</span>\n"
                    + "            </p>\n"
                    + "          </div>\n"
                    + "        </div>\n"
                    + "          <div id=\"collapsable-nav\" class=\"collapsable navbar-collapse\">\n"
                    + "            <ul id=\"nav-list\" class=\"nav navbar-nav navbar-right\">\n"
                    + "              <li>\n"
                    + "                <a href=\"index.html\">\n"
                    + "                  <span class=\"glyphicon glyphicon-remove\"></span><br class=\"hidden-xs\">\n"
                    + "                  Salir\n"
                    + "                </a>\n"
                    + "              </ul>\n"
                    + "          </div>\n"
                    + "        </div>       \n"
                    + "      </div><!-- .container -->\n"
                    + "    </nav><!-- #header-nav -->\n"
                    + "  </header>");
            out.println("<h1>Lista de Libros Y sus datos:</h1>");

            // Tabla con los Libros
            out.println("<div id='listaUsr'>");
            out.println("<table class=\"table\">\n"
                    + "  <thead class=\"thead-light\">\n"
                    + "    <tr>\n"
                    + "      <th scope=\"col\">Id</th>\n"
                    + "      <th scope=\"col\">Titulo</th>\n"
                    + "      <th scope=\"col\">Precio</th>\n"
                    + "      <th scope=\"col\">Genero</th>\n"
                    + "    </tr>\n"
                    + "  </thead>\n"
                    + "  <tbody>");
            out.println(tabla);
            out.println("</tbody></table>");
            out.println("</div>");

            if (usr.getRol() == 1) {
                //formulario para eliminar
                out.println("<div id=\"elim\">");
                out.println("<h1>Para eliminar ingrese el Id del libro:</h1>");
                out.println("<div id=\"del\">");
                out.println("<form action=\"deleteLib\" method=\"POST\">\n"
                        + "                Id del libro:\n"
                        + "                <input type=\"number\" name=\"id\">"
                        + "                 <input type=\"submit\">");
                out.println("</form>");
                out.println("</div>");
                out.println("</div");

                //formulario para Agregar
                out.println("<div id=\"add\">");
                out.println("<h1>Nuevo libro:</h1>");
                out.println("<div id=\"del\">");
                out.println("<form action=\"newLib\" method=\"POST\">\n"
                        + "                 Id:\n"
                        + "                <input type=\"number\" name=\"id\">"
                        + "                Nombre:\n"
                        + "                <input type=\"text\" name=\"titulo\">"
                        + "                  Genero:\n"
                        + "                <input type=\"text\" name=\"genero\">"
                        + "                 Precio:\n"
                        + "                <input type=\"number\" name=\"precio\">"
                        + "                  Stock:\n"
                        + "                  <select name=\"stock\" id=\"stock\">\n"
                        + "                     <option value=\"si\">Si</option>\n"
                        + "                      <option value=\"no\">No</option>\n"
                        + "                    </select>"
                        + "                 <input type=\"submit\">");
                out.println("</form>");
                out.println("</div>");
                out.println("</div>");
            }

            // Footer
            out.println("<footer class=\"panel-footer\">\n"
                    + "    <div class=\"container\">\n"
                    + "      <div class=\"row\">\n"
                    + "        <section id=\"info\" class=\"col-sm-4\">\n"
                    + "          <span>\n"
                    + "            Trabajamos constantemente para brindar el mejor servicio a nuestros usuarios, es por esto que se mantiene la\n"
                    + "            constante actualizacion de esta pagina\n"
                    + "          </span>\n"
                    + "          <hr class=\"visible-xs\">\n"
                    + "        </section>\n"
                    + "        <section id=\"ayuda\" class=\"col-sm-4\">\n"
                    + "          <span>\n"
                    + "            cualquier duda por favor comunicarse con el web-master el cual se encargara de solucionarlo lo mas pronto\n"
                    + "          </span>\n"
                    + "          <hr class=\"visible-xs\">\n"
                    + "        </section>\n"
                    + "        <section id=\"menu\" class=\"col-sm-4\">\n"
                    + "          <span>\n"
                    + "            numeros de contacto , proximamente seran añadidos con otras cosas mas , por el momento lo mas relevante\n"
                    + "          </span>\n"
                    + "          <hr class=\"visible-xs\">\n"
                    + "        </section>\n"
                    + "      </div>\n"
                    + "      <div class=\"text-center\">\n"
                    + "        &copy; Lokitosi 2020\n"
                    + "      </div>\n"
                    + "    </div>\n"
                    + "  </footer>");
            out.println("</body>");
            out.println("<script src=\"./bootstrap/js/jquery-2.1.4.min.js\"></script>\n"
                    + "  <script src=\"./bootstrap/js/bootstrap.min.js\"></script>\n"
                    + "  <script src=\"./bootstrap/js/script.js\"></script>");
            out.println("</html>");
        }

        emf.close();
        em.close();
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
