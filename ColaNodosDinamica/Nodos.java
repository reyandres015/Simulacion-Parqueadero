/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ColaNodosDinamica;

import dto.Carro;

/**
 *
 * @author pipe
 */
public class Nodos {

    Carro dato;
    Nodos siguiente;

    public Nodos(Carro dato) {
        this.dato = dato;
    }

    public Carro getDato() {
        return dato;
    }

    public Nodos getSiguiente() {
        return siguiente;
    }
    
    
}
