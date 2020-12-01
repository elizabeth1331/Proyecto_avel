/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *
 * @author eliss
 */
public class Variables {
    
    
    
        public Hashtable<String,Integer> GuardarVariablesH(String clave, Integer valor,Hashtable<String,Integer> variables){
        
        System.out.print("   jkjhdgjkh"+ clave +"---"+ valor+"\n ");
        variables.put(clave,valor);        
        return variables;
    }
    
    
    public Hashtable<String,Integer> GuardarVariables(String line,Hashtable<String,Integer> variables){
       //Palabra nos sirve para separar la linea en palabras y contabilizarlas
                    String palabra, clave="";
                    Integer valor=0;
                    int numPalabra=0;

                    //Se leen las palabras de la línea
                    StringTokenizer st1 = new StringTokenizer (line);
                    while (st1.hasMoreTokens())
                    {
                        palabra = st1.nextToken();
                        numPalabra++;
                        String aux="";

                        /*Guarda el nombre de la variable o etiqueta*/
                        if(numPalabra==1){
                            clave=palabra=palabra.toUpperCase(); 
                        }

                        //Guarda el contenido de la variable, quitando el $ en el proceso 
                        if((numPalabra==3)){
                            int n=palabra.length();

                            // Se transforma la palabra a cadena
                            char[] p = palabra.toCharArray();
                            //Arreglo auxiliar donde guardara sin $
                            char[] auxP=new  char[n-1];
                            //proceso de guardado
                            for (int j = 0; j < n-1; j++){   
                                auxP[j]=p[j+1];
                            }
                             aux = String.valueOf(auxP);
                             valor=Integer.parseInt(aux);
                        }
                    }
                            //Envia a la funcion para guardar en la HashTable, y revisar que no contenga
                             //otra constante o variable con el mismo nombre
                             GuardarVariablesH(clave,valor,variables);
                 return variables;
                    
}
}