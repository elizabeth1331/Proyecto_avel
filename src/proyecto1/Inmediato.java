/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Clase que analiza una instrucción del modo de direccionamiento inmediato.
 * @author Ari
 */
public class Inmediato {
    Hashtable<String, String> Inmediato;
    Hashtable<String, String> Directo;
    Hashtable<String, String> Extendido;
    Hashtable<String, Integer> BytesInmediato;
    Hashtable<String, Integer> BytesDirecto;
    Hashtable<String, Integer> BytesExtendido;
    
    public Inmediato(){
        this.Inmediato = new Hashtable();
        this.Directo = new Hashtable();
        this.Extendido = new Hashtable();
        this.BytesInmediato = new Hashtable();
        this.BytesDirecto = new Hashtable();
        this.BytesExtendido = new Hashtable();
    }
    /**
     * En este método se analiza la línea que supuestamente contiene una instrucción del modo de direccionamiento inmediato.
     * @param line linea que supuestamente contiene una instrucción del modo de direccionamiento INM.
     * @return OPCODE de la instrucción procesada o mensaje de error.
     */
    public String AnalizarLinea(String line, Mnemonicos m, Hashtable<String,String> variables, int numMemoria){
        //Se llama a mnemónicos para recuperar la lista de instrucciones del modo Inmediato
        metodosDeLectura lectura = new metodosDeLectura();
        Inmediato=m.LeerOpcode("ListaInmediato.txt");
        Directo=m.LeerOpcode("ListaDirecto.txt");
        Extendido=m.LeerOpcode("ListaExtendido.txt");
        BytesInmediato=m.LeerBytes("ListaInmediato.txt");
        BytesDirecto=m.LeerBytes("ListaDirecto.txt");
        BytesExtendido=m.LeerBytes("ListaExtendido.txt");
        
        //Palabra nos sirve para separar la linea en palabras y contabilizarlas
        String palabra;
        int numPalabra=0;
        int d=0;
        boolean imm=false, hex=false, cha=false;
        
        //Esta cadena será la que se devolverá si la sintaxis es correcta, la que guarda la primera palabra y la que nos ayuda a convertir el operando
        String newLine="", instruccion="", op="";;
        
        //Se leen las palabras de la línea
        StringTokenizer st = new StringTokenizer (line);
        while (st.hasMoreTokens())
        {
            palabra = st.nextToken();
            numPalabra++;
            
            /*Se verifica que la primera palabra sea una instrucción del modo de direccionamiento inmediato, y de 
            ser así, se concatena al inicio de la cadena que se desea devolver*/
            if(numPalabra==1){
                palabra=palabra.toUpperCase();
                
                if((Extendido.containsKey(palabra))&&!(Directo.containsKey(palabra))&&!(Inmediato.containsKey(palabra))){
                    instruccion=instruccion.concat(palabra);
                    newLine=newLine.concat(Extendido.get(palabra));
                    d=1;
                }else if((Directo.containsKey(palabra))&&!(Extendido.containsKey(palabra))&&!(Inmediato.containsKey(palabra))){
                    instruccion=instruccion.concat(palabra);
                    newLine=newLine.concat(Directo.get(palabra));
                    d=2;
                }else if((Inmediato.containsKey(palabra))&&!(Directo.containsKey(palabra))&&!(Extendido.containsKey(palabra))){
                    instruccion=instruccion.concat(palabra);
                    newLine=newLine.concat(Inmediato.get(palabra));
                    d=3;
                }else{
                    instruccion=instruccion.concat(palabra);
                    newLine="";
                    d=0;
                }
            }
            
            //Se verifica el operando, el cual corresponde a la segunda palabra de la línea
            if((numPalabra==2)){
                //Si tiene gato, porque es de modo inmediato, se quita
                if(palabra.startsWith("#")){
                    palabra=palabra.substring(1);
                    //System.out.println("Palabra sin #: "+palabra);
                    imm=true;
                }
                
                //Si es numérica pero no hexadecimal, entonces es decimal
                if((esNumero(palabra))&&!(palabra.startsWith("$"))){
                    //Se convierte a número decimal
                    int opN=Integer.parseInt(palabra);
                    //Se convierte el número decimal a hexadecimal y como cadena
                    op=Integer.toHexString(opN).toUpperCase();
                }
                
                //Si es hexadecimal, se quita el signo de pesos
                if(palabra.startsWith("$")){
                    palabra=palabra.substring(1);
                    op=palabra;
                    hex=true;
                }
                
                //En caso de ser caracter
                if(palabra.startsWith("'")){
                    
                    //Se utiliza la cadena aux para separar #' del operando
                    palabra=palabra.substring(1);
                    
                    //Se comprueba si el operando es un solo caracter
                    if(palabra.length()==1){
                        char character = palabra.charAt(0);
                        
                        //Se convierte el caracter a decimal
                        int ascii = (int)character;
                        
                        //Se convierte el decimal a hexadecimal y se guarda como cadena de mayúsculas
                        op= Integer.toHexString(ascii).toUpperCase();
                        cha=true;
                        
                    }else{
                        String mensaje = line+"\n\t\t\t^\u001B[31m Error 007: MAGNITUD DE  OPERANDO ERRONEA\u001B[0m\n";
                        //Guardamos la salida de la primer pasada
                        Output outPut = new Output();
                        outPut.mensaje = mensaje;
                        metodosDeLectura.salidas.add(outPut);
                        
                        return line+"\n\t\t\t^Error 007: MAGNITUD DE  OPERANDO ERRONEA";
                    }
                    
                }
                
                //En caso de ser constante u op del modo inmediato
                if(imm){
                    if(variables.containsKey(palabra)){
                        //El operando es una constante porque tenía #
                        op=variables.get(palabra);
                    }else if(((!variables.containsKey(palabra))&&(!hex))&&(!esNumero(palabra))&&(!cha)){
                        String mensaje = line+"\n\t\t\t^\u001B[31m Error 001: CONSTANTE INEXTISTENTE\u001B[0m\n";
                        //Guardamos la salida de la primer pasada
                        Output outPut = new Output();
                        outPut.mensaje = mensaje;
                        metodosDeLectura.salidas.add(outPut);
                        return line+"\n\t\t\t^Error 001: CONSTANTE INEXTISTENTE";
                    }
                    //El caso en que tuviese # y no se encontrara en la lista de variables o constantes también 
                    //implica que puede ser del modo inmediato.
                    
                //En caso de ser variable u op del modo directo o extendido
                }else{
                    if(variables.containsKey(palabra)){
                        //El operando es una variable porque no tenía #
                        op=variables.get(palabra);
                    }else if(((!variables.containsKey(palabra))&&(!hex))&&(!esNumero(palabra))&&(!cha)){
                        String mensaje = line+"\n\t\t\t^\u001B[31m Error 002: VARIABLE INEXTISTENTE\u001B[0m\n";
                        //Guardamos la salida de la primer pasada
                        Output outPut = new Output();
                        outPut.mensaje = mensaje;
                        metodosDeLectura.salidas.add(outPut);
                        
                        return line+"\n\t\t\t^Error 002: VARIABLE INEXTISTENTE";
                    }
                }
                
                switch (d){
                    case 1:
                        //Extendido
                        if(BytesExtendido.get(instruccion)==(op.length()/2)&&(op.length()%2==0)){
                            //Si coinciden, se agrega a la cadena que será regresada
                            String mensaje = "\n\u001B[45;30m"+newLine+"\u001B[0m";
                            newLine=newLine.concat(op);
                            mensaje = mensaje + "\u001B[35m"+op+"\u001B[0m"+"\t\t\t"+line+"\n";
                            
                             //Cálculo del número de espacios de memoria utilizado hasta el momento
                             metodosDeLectura.numMemoria = metodosDeLectura.numMemoria + BytesExtendido.get(instruccion)+(Extendido.get(instruccion)).length()/2;
                            //Guardamos la salida de la primer pasada
                            Output outPut = new Output();
                            outPut.mensaje = mensaje;
                            metodosDeLectura.salidas.add(outPut);
                        }
                        break;
                    case 2:
                        //Directo
                        if(BytesDirecto.get(instruccion)==(op.length()/2)&&(op.length()%2==0)){
                            //Si coinciden, se agrega a la cadena que será regresada
                            String mensaje = "\n\u001B[46;30m"+newLine+"\u001B[0m";
                            newLine=newLine.concat(op);
                            mensaje = mensaje + "\u001B[36m"+op+"\u001B[0m"+"\t\t\t"+line+"\n";
                            
                             //Cálculo del número de espacios de memoria utilizado hasta el momento
                            metodosDeLectura.numMemoria = metodosDeLectura.numMemoria + BytesDirecto.get(instruccion)+(Directo.get(instruccion)).length()/2;
                            //Guardamos la salida de la primer pasada
                            Output outPut = new Output();
                            outPut.mensaje = mensaje;
                            metodosDeLectura.salidas.add(outPut);
                        }
                        break;
                    case 3:
                        //Inmediato
                        if(BytesInmediato.get(instruccion)==(op.length()/2)&&(op.length()%2==0)){
                            //Si coinciden, se agrega a la cadena que será regresada
                            String mensaje = "\n\u001B[43;30m"+newLine+"\u001B[0m";
                            newLine=newLine.concat(op);
                            mensaje = mensaje + "\u001B[33m"+op+"\u001B[0m"+"\t\t\t"+line+"\n";
                            
                             //Cálculo del número de espacios de memoria utilizado hasta el momento
                            metodosDeLectura.numMemoria = metodosDeLectura.numMemoria + BytesInmediato.get(instruccion)+(Inmediato.get(instruccion)).length()/2;
                            //Guardamos la salida de la primer pasada
                            Output outPut = new Output();
                            outPut.mensaje = mensaje;
                            metodosDeLectura.salidas.add(outPut);
                        }
                        break;
                    case 0:
                        //Puede ser inmediato, directo o exacto
                        if((imm)){
                            //Tenía #, entonces es del modo inmediato
                            newLine=newLine.concat(Inmediato.get(instruccion));
                            if((BytesInmediato.containsKey(instruccion))&&(BytesInmediato.get(instruccion)==(op.length()/2)&&(op.length()%2==0))){
                                //Si coinciden, se agrega a la cadena que será regresada
                                String mensaje = "\n\u001B[43;30m"+newLine+"\u001B[0m";
                                newLine=newLine.concat(op);
                                mensaje = mensaje +"\u001B[33m"+op+"\u001B[0m"+"\t\t\t"+line+"\n";
                                 //Cálculo del número de espacios de memoria utilizado hasta el momento
                                    metodosDeLectura.numMemoria = metodosDeLectura.numMemoria + BytesInmediato.get(instruccion)+(Inmediato.get(instruccion)).length()/2;
                                //Guardamos la salida de la primer pasada
                                Output outPut = new Output();
                                outPut.mensaje = mensaje;
                                metodosDeLectura.salidas.add(outPut);
                            }else{
                                String mensaje = line+"\n\t\t\t^\u001B[31m Error 007: MAGNITUD DE  OPERANDO ERRONEA\u001B[0m\n";
                                //Guardamos la salida de la primer pasada
                                Output outPut = new Output();
                                outPut.mensaje = mensaje;
                                metodosDeLectura.salidas.add(outPut);
                                return line+"\n\t\t\t^Error 007: MAGNITUD DE  OPERANDO ERRONEA";
                            }
                        }else{
                            //No tenía #, entonces puede ser de los modos directo o extendido
                            
                            if((BytesDirecto.containsKey(instruccion))&(BytesDirecto.get(instruccion))==(op.length()/2)&(op.length()%2==0)){
                                //Si coinciden, se agrega a la cadena que será regresada
                            
                                newLine=newLine.concat(Directo.get(instruccion));
                                String mensaje = "\n\u001B[46;30m"+newLine+"\u001B[0m";
                                newLine=newLine.concat(op);
                                mensaje = mensaje + "\u001B[36m"+op+"\u001B[0m"+"\t\t\t"+line+"\n";
                                
                                //Cálculo del número de espacios de memoria utilizado hasta el momento
                                metodosDeLectura.numMemoria = metodosDeLectura.numMemoria + BytesDirecto.get(instruccion)+(Directo.get(instruccion)).length()/2;
                                //Guardamos la salida de la primer pasada
                                Output outPut = new Output();
                                outPut.mensaje = mensaje;
                                metodosDeLectura.salidas.add(outPut);
                            }else if((BytesExtendido.containsKey(instruccion))&&(BytesExtendido.get(instruccion)==(op.length()/2)&&(op.length()%2==0))){
                                //Si coinciden, se agrega a la cadena que será regresada
                            
                                newLine=newLine.concat(Extendido.get(instruccion));
                                String mensaje = "\n\u001B[45;30m"+newLine+"\u001B[0m";
                                newLine=newLine.concat(op);
                                mensaje = mensaje + "\u001B[35m"+op+"\u001B[0m"+"\t\t\t"+line+"\n";
                                 //Cálculo del número de espacios de memoria utilizado hasta el momento
                                metodosDeLectura.numMemoria = metodosDeLectura.numMemoria + BytesExtendido.get(instruccion)+(Extendido.get(instruccion)).length()/2;
                                //Guardamos la salida de la primer pasada
                                Output outPut = new Output();
                                outPut.mensaje = mensaje;
                                metodosDeLectura.salidas.add(outPut);
                            }else{
                                String mensaje = line+"\n\t\t\t^\u001B[31m Error 007: MAGNITUD DE  OPERANDO ERRONEA\u001B[0m\n";
                                //Guardamos la salida de la primer pasada
                                Output outPut = new Output();
                                outPut.mensaje = mensaje;
                                metodosDeLectura.salidas.add(outPut);
                                return line+"\n\t\t\t^Error 007: MAGNITUD DE  OPERANDO ERRONEA";
                            }
                        }
                        break;
                }
            }
            
            if((numPalabra==3)&&(palabra.startsWith("*"))){
                //Es un comentario, no es necesario realizar nada más
            }else if((numPalabra==3)&&(!palabra.startsWith("*"))){
                String mensaje = line+"\n\t\t\t^\u001B[31m Error 000: ERROR DE SINTAXIS\u001B[0m\n";
                //Guardamos la salida de la primer pasada
                Output outPut = new Output();
                outPut.mensaje = mensaje;
                metodosDeLectura.salidas.add(outPut);
                
                return line+"\n\t\t\t^Error 000: ERROR DE SINTAXIS";
            }
        }
        if (numPalabra<2){
            String mensaje = line+"\n\t\t\t^\u001B[31m Error 005: INSTRUCCIÓN CARECE DE  OPERANDO(S)\u001B[0m\n";
            //Guardamos la salida de la primer pasada
            Output outPut = new Output();
            outPut.mensaje = mensaje;
            metodosDeLectura.salidas.add(outPut);
            
            return line+"\n\t\t\t^Error 005: INSTRUCCIÓN CARECE DE  OPERANDO(S)";
        }
        newLine=newLine.concat("\t\t\t"+line);
        return newLine;
    }
    
    
    public boolean esNumero(String operando){
        try{
            Integer.parseInt(operando);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
}
