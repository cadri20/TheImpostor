package fuentes;

import java.time.LocalTime;
import java.util.StringTokenizer;

/**
 *
 * @author Hp
 */
public class DiaDeLaSemana {
    private Dia dia;
    private LocalTime horaInicio;
    private LocalTime HoraFinal;

    public DiaDeLaSemana(Dia dia, String horaInicio, String horaFinal) {
        this.dia = dia;
        
        StringTokenizer tokenizer = new StringTokenizer(horaInicio, ":");
        this.horaInicio = LocalTime.of(Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()));
        
        StringTokenizer tokenizer2 = new StringTokenizer(horaFinal, ":");
        this.HoraFinal = LocalTime.of(Integer.parseInt(tokenizer2.nextToken()), Integer.parseInt(tokenizer2.nextToken()));
    }

    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFinal() {
        return HoraFinal;
    }

    public void setHoraFinal(LocalTime HoraFinal) {
        this.HoraFinal = HoraFinal;
    }

    
    @Override
    public String toString() {
        return "DiaDeLaSemana{" + "dia=" + dia + ", horaInicio=" + horaInicio + ", HoraFinal=" + HoraFinal + '}';
    }
    
    
}
