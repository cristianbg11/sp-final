package controlador;

import entidad.Estudiante;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import repositorio.EstudianteRepository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstudianteController {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    final Session secion = getSession();
    EntityManager em = getSession();

    @Autowired
    private EstudianteRepository estudianteRepository;

    @GetMapping(value = "/")
    public String index(Model model) {
        Map<String, Object> attributes = new HashMap<>();
        List<Estudiante> estudiantes = em.createQuery("select e from Estudiante e", Estudiante.class).getResultList();
        attributes.put("listado",estudiantes);
        return "index";
    }

    @GetMapping("/create")
    public String showSignUpForm(Estudiante estudiante) {
        return "add-est";
    }

    @PostMapping("/insertar")
    public String addEst(@Valid Estudiante estudiante, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "in-sertar";
        }

        estudianteRepository.save(estudiante);
        model.addAttribute("listado", estudianteRepository.findAll());
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante invalido:" + id));

        model.addAttribute("edicion", estudiante);
        return "update-est";
    }

    @PostMapping("/actualizar/{id}")
    public String updateEstudent(@PathVariable("id") int id, @Valid Estudiante estudiante,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            estudiante.setId(id);
            return "actuali-zar";
        }

        estudianteRepository.save(estudiante);
        model.addAttribute("listado", estudianteRepository.findAll());
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteEstudent(@PathVariable("id") long id, Model model) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante invalido:" + id));
        estudianteRepository.delete(estudiante);
        model.addAttribute("estudantes", estudianteRepository.findAll());
        return "index";
    }
}
