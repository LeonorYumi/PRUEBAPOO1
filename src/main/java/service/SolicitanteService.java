package service;

import dao.SolicitanteDao;
import model.Solicitante;
import java.time.LocalDate;
import java.time.Period;

public class SolicitanteService {

    // Instanciamos el DAO para poder usarlo
    private SolicitanteDao dao = new SolicitanteDao();
    private static final int EDAD_MINIMA = 18;

    public Integer registrarSolicitante(Solicitante s) throws Exception {

        // 1. VALIDACIÓN: Longitud de 10 dígitos
        if (s.getCedula() == null || s.getCedula().length() != 10) {
            throw new Exception("La cédula debe tener exactamente 10 dígitos.");
        }

        // 2. VALIDACIÓN: Solo números (matches "[0-9]+")
        if (!s.getCedula().matches("[0-9]+")) {
            throw new Exception("La cédula solo puede contener números.");
        }

        // 3. VALIDACIÓN: Mayoría de edad
        if (s.getFechaNacimiento() == null) {
            throw new Exception("La fecha de nacimiento es obligatoria.");
        }

        int edad = Period.between(s.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new Exception("El solicitante debe ser mayor de edad (" + EDAD_MINIMA + " años).");
        }

        // --- AQUÍ ESTABA EL ERROR ---
        // Cambiamos 'insertarConTramite' por 'create' para que coincida con tu DAO
        return dao.create(s);
    }
}