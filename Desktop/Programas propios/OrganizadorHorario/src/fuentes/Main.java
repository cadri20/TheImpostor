package fuentes;

/**
 *
 * @author Hp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ListaMaterias lista = new ListaMaterias();
        Materia mat1 = new Materia("Fisica", Dia.LUNES, "10:0", "20:0");
        lista.add(mat1);
        System.out.println(lista.toString());
        
        System.out.println(mat1.dia.getHoraFinal().toString());
       
    }
    
}
