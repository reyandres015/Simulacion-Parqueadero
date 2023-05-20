/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ColaNodosDinamica;

import dto.Carro;

/**
 *
 * @author pipe
 */
public class TDAColas {

    private Nodos back; //ultimo en la cola
    private Nodos front; //Primero en la cola
    private int numeroElementos;

    public TDAColas() {
        this.back = null;
        this.front = null;
        this.numeroElementos = 0;
    }

    /**
     * Verifica si la pila contiene elementos
     *
     * @return true - Pila Vacia; false - Pila con elementos
     */
    public boolean isEmpty() {
        return front == null || back == null;
    }

    /**
     * Ingresa un nodo a la cima de la pila con el dato que recibe por
     * parametro.
     *
     * @param dato
     */
    public boolean enqueue(Carro dato) {
        Nodos temp = new Nodos(dato);
        if (isEmpty()) {
            front = temp;
            back = temp;
        } else {
            back.siguiente = temp;
            back = temp;
        }
        numeroElementos++;
        return true;
    }

    /**
     * Retira el nodo de la cima de la pila. Primero verfica si la lista esta
     * vacia
     *
     * @return Nodo que retira.
     */
    public Carro dequeue() {
        if (!isEmpty()) {
            Nodos temp = front;
            front = temp.siguiente;
            numeroElementos--;
            return temp.getDato();
        } else {
            return null;
        }
    }

    /**
     * @return Nodo de la cabeza de la cola.
     */
    public Nodos peekFront() {
        return front;
    }

    public Nodos peekBack() {
        return back;
    }

    /**
     * @return Tamaño de la pila.
     */
    public int size() {
        return numeroElementos;
    }

}
