package fuentes;

/**
 *
 * @author Hp
 */
public class Materia {
    String nombreMateria;
    DiaDeLaSemana dia;

    public Materia(String nombreMateria, Dia dia, String horaInicio, String horaFinal) {
        this.nombreMateria = nombreMateria;
        this.dia = new DiaDeLaSemana(dia, horaInicio, horaFinal);
    }

    @Override
    public String toString() {
        return "Materia{" + "nombreMateria=" + nombreMateria + ", dia=" + dia + '}';
    }
    

    
}
