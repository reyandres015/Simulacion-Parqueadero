package ColaNodosDinamica;

import java.time.LocalDateTime;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author reyan
 */
public class ActualizarAtributoParalelo implements Runnable {

    private Nodos nodo;

    public ActualizarAtributoParalelo(Nodos nodo) {
        this.nodo = nodo;
    }

    @Override
    public void run() {
        nodo.getDato().setTiempo(LocalDateTime.now());
        
    }
}
