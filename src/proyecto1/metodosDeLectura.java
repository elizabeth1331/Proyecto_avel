/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//C:\Users\81664\Desktop\Proyecto 2.0\Proyecto
//C:\Users\81664\Desktop\Proyecto 2.0\Proyecto\prueba_salto.txt
package proyecto1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Esta clase hace el análisis principal del código del archivo .asc
 * @author todas
 */



public class metodosDeLectura {
    /**
     * En este método se lee el archivo .asc línea por línea para su análisis
     * @param name Nombre del archivo .asc
     */
    
    //Se crea HashTable donde guardar las variables o/y constantes
    Hashtable<String,String> variables=new Hashtable();
    static int numMemoria = 0;
    static List<Output> salidas = new ArrayList<Output>();
    
    public void Lectura(String name){
        //Variable que ayuda a llevar el conteo de líneas que ocupan memoria 
        int numPalabra=0, inicio = 0, org=0;
        numMemoria = 0;
        String error = "", memHexa ="", memHexaAux = "", inicioHexa= "", mensaje = "";
        boolean end=false, wrong=false;
        
        /*Se crean varios archivos dentro de la carpeta del proyecto, cada archivo guarda una lista de las
        instrucciones correspondientes a cada modo de direccionamiento */
        Mnemonicos m=new Mnemonicos();
        m.insertar();
        
        //Creamos una instancia de la clase que nos sirve para guardar variables, constantes y etiquetas
        Var_Cons_Etiq VCE= new Var_Cons_Etiq();
        
        //Se elimina "archivo.txt" si existe
        
        File f=new File("Archivo.txt");
        if (f.exists()){
            f.delete();
        }
        
        String LST=name.substring(0,name.length()-3);
        f=new File(LST + "LST");
        if (f.exists()){
            f.delete();
        }
        
        f=new File(LST + "S19");
        if (f.exists()){
            f.delete();
        }
        
        Scanner file;
        String line = "";
        try {
            file = new Scanner(new FileReader(name));
            int num=0;
            while (file.hasNextLine()&&end==false) {
                num++;
                line = file.nextLine();
                //System.out.println("*****LIRIO******La memoria es:"+numMemoria);
                
                //System.out.println(line); // Palabra que lee
                if(line.startsWith("*")){
                     //No imprimimos la memoria
                       memHexaAux = " ";
                    //Se salta a la sig línea
                    error = line;
                }else if(line.contains("EQU")||line.contains("equ")){
                        //No imprimimos la memoria
                        memHexaAux = " ";
                        error = GuardarVariables(line, variables);
                }else if(line.startsWith("\t")||line.startsWith(" ")){
                    String lineAux=line;
                    while(lineAux.startsWith(" ")){
                            lineAux=lineAux.substring(1);
                    }
                    while(lineAux.startsWith("\t")){
                            lineAux=lineAux.replace("\t","");
                    }
                    if(lineAux.startsWith("*")){
                     //No imprimimos la memoria
                       memHexaAux = " ";
                    //Se salta a la sig línea
                    error = line;
                    }else if(lineAux.startsWith("ORG")||lineAux.startsWith("org")){
                        //No imprimimos la memoria
                        memHexaAux = " ";
                        int numP=0;
                        if(lineAux.startsWith("ORG"))
                            lineAux=lineAux.replace("ORG","");
                        if(lineAux.startsWith("org"))
                                lineAux=lineAux.replace("org","");
                        while(lineAux.startsWith(" ")){
                            lineAux=lineAux.substring(1);
                        }    
                        //Asignar el valor de inicio de memoria
                        StringTokenizer st = new StringTokenizer (lineAux);
                        String palabra = "";
                        while(st.hasMoreTokens()){
                            palabra = st.nextToken();
                            numP++;
                            if(palabra.startsWith("*")){
                                numP--;
                                break;
                            }else if(palabra.startsWith("$")){
                                org=Integer.parseInt(palabra.substring(1));
                                inicio = Integer.parseInt(palabra.substring(1),16);
                                memHexa = Integer.toHexString(inicio).toUpperCase();
                            }else{
                                break;
                            }
                            if(numP==1){
                                error= memHexa + "\t" + line;
                            }else{
                                error=line+"\n\t\t\t^Error: Sintaxis incorrecta";
                            }
                        }
                    }else if(lineAux.startsWith("FCB")||lineAux.startsWith("fcb")){
                        boolean bien=false;
                        int numB = 0;
                        if(lineAux.startsWith("FCB"))
                            lineAux=lineAux.replace("FCB","");
                        if(lineAux.startsWith("fcb"))
                            lineAux=lineAux.replace("fcb","");
                        while(lineAux.startsWith(" ")){
                            lineAux=lineAux.substring(1);
                        }
                        //Separados por coma o por espacio
                        if(lineAux.contains(",")){
                            lineAux=lineAux.replace(","," ");
                        }
                        StringTokenizer st = new StringTokenizer (lineAux);
                        //Leemos la ínea hasta encontrar el primer espacio
                        String palabra = "";
                        error="";
                        while(st.hasMoreTokens()){
                            palabra = st.nextToken();
                            //System.out.println("Palabra a analizar:"+palabra);
                            if(palabra.startsWith("*")){
                                bien=true;
                                break;
                            }else if(palabra.startsWith("$")){
                                palabra = palabra.substring(1);
                                //System.out.println("Palabra substring(1):"+palabra);
                                error = error + palabra;
                                numB=numB+1;
                                //System.out.println("numB al momento de "+lineAux+" es: "+numB);
                                bien=true;
                            }else{
                                bien=false;
                                error="";
                                error=line+"\n\t\t\t^Error: Sintaxis incorrecta";
                                memHexaAux=" ";
                                break;
                            }
                        }
                        if(lineAux.equals("")){
                            //No se calcula la dirección porque no tiene operandos
                            memHexaAux=" ";
                            //Se imprime la original porque la auxiliar está vacia
                            error = line;
                        }else if(bien){
                            //Se concatena la original porque la auxiliar no tiene la instruccion fcb
                            
                            error=error+line;
                            //System.out.println("Esta es la que se imprime ¿no?"+error);
                            //Se debe calcular la dirección porque tiene operandos
                            numMemoria = numMemoria + numB;
                            memHexaAux=Integer.toHexString(inicio+numMemoria).toUpperCase();
                        }
                    }else if(lineAux.startsWith("END")||lineAux.startsWith("end")){
                        if(lineAux.contains("END"))
                            lineAux=lineAux.replace("END","");
                        if(lineAux.contains("end"))
                            lineAux=lineAux.replace("end","");
                        while(lineAux.startsWith(" ")){
                            lineAux=lineAux.substring(1);
                        }
                        if(lineAux.startsWith("*")||lineAux.equals("")){
                            end=true;
                            error=line;
                        }else{
                            mensaje = line + "\u001B[31m\n\t\t\t^Error: Sintaxis incorrecta.\u001B[0m\n";
                            //Guardamos la salida de la primer pasada
                            Output outPut = new Output();
                            outPut.mensaje = mensaje;
                            metodosDeLectura.salidas.add(outPut);
                            
                        }
                        //No imprimimos la memoria
                        memHexaAux = "  ";
                    }
                else{
                        memHexa = Integer.toHexString(inicio+numMemoria).toUpperCase();
                        memHexaAux = memHexa;
                        error = DifModoDeDireccionamiento(line, m, numMemoria, VCE, num, inicio);
                    }
                    
                }else if(!line.equals("")){
                    //No imprimimos la memoria
                    memHexaAux = " ";
                    //Con este objeto podemos dividir la cadena en sub cadenas. 
                    StringTokenizer st = new StringTokenizer (line);
                    //Leemos la ínea hasta encontrar el primer espacio
                    String palabra = st.nextToken();
                    //Convertimos la palabra en mayúsculas 
                    String auxPalabra = palabra.toUpperCase();
                    
                        if(EsInstruccion(auxPalabra, m)!=0){
                        //Error 009
                        //Pero debe revisar todos los archivos para corroborar que la primera "palabra" sea una instrucción,
                        //Si no la encuentra entonces es una ETIQUETA
                         mensaje = line + "\u001B[31m\n\t\t\t^Error 09: INSTRUCCIÓN CARECE DE AL MENOS UN ESPACIO RELATIVO AL MARGEN\u001B[0m\n";
                         //Guardamos la salida de la primer pasada
                         Output outPut = new Output();
                         outPut.mensaje = mensaje;
                         metodosDeLectura.salidas.add(outPut);
                            
                        
                        error = line+"\n\t\t\t^Error 09: INSTRUCCIÓN CARECE DE AL MENOS UN ESPACIO RELATIVO AL MARGEN"; 
                        }else{
                            error = VCE.agregarEtiqueta(palabra, numMemoria + inicio);
                            if(!variables.contains(palabra)){
                                Integer intAux = numMemoria + inicio;
                                variables.put(palabra, Integer.toHexString(intAux).toUpperCase());
                                System.out.println("--Lista var:");
                                System.out.println(variables);
                            }
                            
                            //System.out.println(VCE.Etiquetas);
                        }
                    
                }else if(line.equals("")){
                    error=line;
                }
                if(error.contains("^")){
                    wrong=true;
                    //System.out.println("EN ESTE MOMENTO WRONG CAMBIO A TRUE CON+"+error);
                }
                try {
                    FileWriter fstream = new FileWriter("Archivo.txt", true);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(num+" A"+" "+memHexaAux+"\t"+error+"\n");
                    out.close();
                } catch (IOException ex) {
                    System.out.println("Error: "+ex.getMessage());
                }
                
            }
            
            if(!end){
                
                mensaje = "\u001B[31m\n\t\t\t^Error 010: NO SE ENCUENTRA END\u001B[0m\n";
                //Guardamos la salida de la primer pasada
                Output outPut = new Output();
                outPut.mensaje = mensaje;
                metodosDeLectura.salidas.add(outPut);
                error="\n\t\t\t^Error 010: NO SE ENCUENTRA END";
                
                try {
                    FileWriter fstream = new FileWriter("Archivo.txt", true);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(error);
                    out.close();
                } catch (IOException ex) {
                    System.out.println("Error: "+ex.getMessage());
                }
                    file.close();
                }            
        }
        catch (FileNotFoundException e){
            System.out.println("Error al leer el archivo, " + e.getMessage());
        }
        
        //Segunda pasada
        String newLine = "";
        int numLinea = -1;
        int lineaSalto = 0;
        boolean excepcion = false; 
        String palabra;
        String primerPalabra = "";
        Output op = new Output();
        int ultimoSalto = 0, saltoAnterior = 0;
        List <String> segunda = new ArrayList();
        int numsalto =0;
        
        while (!salidas.isEmpty()){ //Se va vaciando la lista de mensajes
            //numsalto ++;
            //System.out.println("El salto es: " + numsalto);
            saltoAnterior = segunda.size();           
            while(lineaSalto==0&&!salidas.isEmpty()){
                //Sacamos un elemento de la lista hasta que alguno corresponda a una línea con salto
                op =salidas.remove(0);
                if (op.salto){
                    lineaSalto = op.linea;
                    excepcion = op.excepcion;
                }else{
                    System.out.print(op.mensaje);
                }
                 
            }
            
            try{
                file = new Scanner(new FileReader("Archivo.txt"));
                
                /*Mientras no se llegue al final del archivo y no llegemos a una línea con salto, 
                 se irán transcribiendo las líneas que no tienen salto e iremos eliminando las líneas leidas. 
                */
                while (file.hasNextLine()&& numLinea != lineaSalto){ 
                    //System.out.println("Escribiendo archivo LST");
                    
                    /*for(int i = 0; i<=numLinea; i++){
                        line = file.nextLine();
                    }*/
                    line = file.nextLine();
                    System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line);
                    if(st.hasMoreTokens())
                        primerPalabra = st.nextToken();
                    
                    /*Se obtiene el número de la línea que se está leyendo para saber si corresponde o no a un salto*/
                    if (!primerPalabra.startsWith("^")&&st.countTokens()>2)
                        numLinea = Integer.parseInt(primerPalabra);
                    //System.out.println("numLinea = " + numLinea);
                    if (numLinea == lineaSalto){
                        if(excepcion){
                            Excepciones EXC=new Excepciones();
                            line=EXC.analizarLinea(line, m, VCE, variables, numMemoria, 2, numLinea, inicio);
                        }else{
                            Relativo REL=new Relativo();
                            line=REL.RevisarLinea(line, m, VCE, numMemoria,2, numLinea, inicio);
                            if (Relativo.error){
                                ultimoSalto = saltoAnterior;
                            }
                        }
                    }
                    if(ultimoSalto!=0){
                        if(ultimoSalto < numLinea){
                            segunda.add(line);
                            System.out.println("------Se agrega: "+ line);
                        }
                    }else{
                        segunda.add(line);
                    }
                    
                    
                        
                    
                }
                ultimoSalto = segunda.size();
                file.close();
                
            }catch(FileNotFoundException e){
                System.out.println("Error al leer el archivo, " + e.getMessage());
            }
            lineaSalto = 0;
            
            System.out.println("-------LISTA DE 2da pasada--------");
            System.out.println("tamaño: " + salidas.size());
            
            for(int k = 0; k<segunda.size();k++){
                System.out.println(segunda.get(k));
                
            }
        }
        
                       
        while(!segunda.isEmpty()){
            String l = segunda.remove(0);
            try {
                FileWriter fstream = new FileWriter(LST+"LST", true);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(l + "\n");
                out.close();
            } catch (IOException ex) {
                System.out.println("Error: "+ex.getMessage());
            }  
        }
        //System.out.println("**********************El valor de wrong es:"+wrong);
        if(wrong==false){
            //System.out.println("***************************No hay errores, se genera el S19");
            // Generar el S19
            Integer noLinea = 0;
            String lineaNueva = "",aux="";
            String s19="";
            try{
                file = new Scanner(new FileReader(LST + "LST"));
                 while (file.hasNextLine()){
                    noLinea++;
                    lineaNueva=file.nextLine();
                    aux=lineaNueva;
                    aux=aux.replaceFirst("A","");
                    aux=aux.replaceFirst(noLinea.toString(),"");
                    if(aux.contains("*")){
                        int asterisco=aux.indexOf("*");
                        int sub=(aux.length()-1)-asterisco;
                        aux=aux.substring(0,aux.length()-(sub+1));
                    }
                    if(aux.contains("FCB")){
                        aux=aux.replace("FCB","");
                    }else if(line.contains("fcb")){
                        aux=aux.replace("fcb","");
                    }
                    int noWord=0;
                    //System.out.println("Linea hasta el momento:"+aux);
                    StringTokenizer st = new StringTokenizer (aux);
                    //s19="";
                    while(st.hasMoreTokens()){
                        String word=st.nextToken();
                        noWord++;
                        if((aux.contains("EQU")||aux.contains("equ"))&&noWord==1&&word.length()%2==0){
                            //System.out.println("word: "+word);
                            s19=s19.concat(word);
                            //System.out.println("Nuevo s19:"+s19);
                            break;
                        }else if(aux.equals("")){
                            //no se hace nada porque es un salto de línea
                            break;
                        }else if((aux.contains("ORG")||aux.contains("org"))&&noWord==1&&word.length()%2==0){
                            //System.out.println("word: "+word);
                            //s19=s19.concat(word);
                            //System.out.println("Nuevo s19:"+s19);
                            break;
                        }else if(aux.contains("END")||aux.contains("end")){
                            //no se agrega nada
                            break;
                        }else if(noWord==2&&word.length()%2==0){
                            //System.out.println("word: "+word);
                            s19=s19.concat(word);
                            //System.out.println("Nuevo s19:"+s19);
                            break;
                        }
                    }
                 }
                 file.close();
           }catch(Exception e){
               System.out.println("No se pudo leer el archivo");
           }
           
            Integer ini=org;
            String mem = ini.toString();
            int l=s19.length();
            
            //System.out.println("Memoria inicial="+mem);
            System.out.println("s19 es: "+s19);
            //System.out.println("Longitud de s19:"+s19.length());
            
            try {
                FileWriter fstream = new FileWriter(LST+"S19",true);
                BufferedWriter out = new BufferedWriter(fstream);
                while(!s19.equals("")){
                    out.write("<"+mem+">");
                    for(int j=0;j<32;j=j+2){
                        if(!s19.isEmpty()){
                            out.write(s19.charAt(0));
                            out.write(s19.charAt(1));
                            out.write(" ");
                            s19=s19.substring(2);
                        }else{
                            break;
                        }
                    }
                    out.write("\n");
                    ini=ini+10;
                    mem=ini.toString();
                    System.out.println("New mem:"+mem);
                }
                out.close();
            } catch (IOException ex) {
                System.out.println("Error: "+ex.getMessage());
            }
        }
    }
    /**
     * En este método envía la línea que se está analizando a su respectiva clase para analisar cada método de direccionamiento por separado.
     * @param line es la línea del código que se está analizando.
     * @param m es una instancia de la clase Mnemónicos, nos ayuda a recuperar las istas de Mnemónicos guardades en archivos. 
     * @return newLine Es la línea que se analizó más su OPCODE o el error detectado.
     */
    public String DifModoDeDireccionamiento(String line, Mnemonicos m, int numMemoria, Var_Cons_Etiq VCE, int numLinea, int inicio){
        Inmediato IMM= new Inmediato();
        Relativo REL=new Relativo();
        Inherente INH=new Inherente();
        Excepciones EXC=new Excepciones();
        String palabra="";
        String linea;
        //Directo y Extendido
        
        String newLine="";
        int op;
        
        if(line.contains("BCLR")||line.contains("BRCLR")||line.contains("BRSET")||line.contains("BSET")||line.contains("bclr")||line.contains("brclr")||line.contains("brset")||line.contains("bset")){
            /*Si la linea contiene alguna de las 4 excepciones, va la clase de excepciones para verificar si 
            es una excepcion o tratarla como un mnemonico comun.*/
            newLine=EXC.analizarLinea(line, m, VCE, variables, numMemoria, 1, numLinea, inicio);
            //Si se regresa un error al analizar la línea, se devuelve la cadena con el error
            //f(newLine.contains("No es mnemonico excepcional") || newLine.contains("ERROR")){
            //return newLine;
            //}
        }else if(line.contains(",")){
            
            if(line.contains(",X")||line.contains(",x")){
                //Si la línea contiene es de tipo ",x" o ",X", se utiliza la clase del método de direccionamiento indexado.
                IndexadoX IND= new IndexadoX();
                newLine=IND.revisarLineaX(line, m, variables, numMemoria);
                //Si se regresa un error al analizar la línea, se devuelve la cadena con el error
                if(newLine.contains("Error")){
                    return newLine;
                /*Si no se encuentra un error, se agrega el código frente al OPCODE dejando un espacio de 3 tabuladores
                y se regresan ambos*/
                }else{
                    newLine=newLine.concat("\t\t\t"+line);
                    return newLine;
                }
                
            }else if(line.contains(",Y")||line.contains(",y")){
                
                //Si la línea contiene es de tipo ",y" o ",Y", se utiliza la clase del método de direccionamiento indexado.
                IndexadoY IND= new IndexadoY();
                linea=newLine=IND.revisarLineaY(line, m, variables, numMemoria);
                //Si se regresa un error al analizar la línea, se devuelve la cadena con el error
                if(newLine.contains("Error")){
                    return newLine;
                /*Si no se encuentra un error, se agrega el código frente al OPCODE dejando un espacio de 3 tabuladores
                y se regresan ambos*/
                }else{
                    newLine=newLine.concat("\t\t\t"+line);
                    return newLine;
                }
            }
        }else if(!line.equals("")){
            StringTokenizer st = new StringTokenizer (line);
            //Leemos la ínea hasta encontrar el primer espacio
            if(st.hasMoreTokens()){
                palabra = st.nextToken();
            }
            //Convertimos la palabra en mayúsculas 
            palabra = palabra.toUpperCase();
            op=EsInstruccion(palabra,m);
            switch (op){
                case 0:
                    System.out.println("\u001B[31m Error 004: MNEMÓNICO INEXISTENTE\u001B[0m");
                    newLine=line+"\n\t\t\t^Error 004: MNEMÓNICO INEXISTENTE";
                    break;
                case 1:
                    //System.out.println(palabra+" Es instrucción del modo inmediato, directo o extendido");
                    newLine=IMM.AnalizarLinea(line, m, variables, numMemoria);
                    break;
                case 2:
                    //System.out.println(palabra+" Es unstrucción del modo inherente");
                    newLine=INH.AnalizarLinea(line, m, numMemoria);
                    break;
                case 3:
                    //System.out.println(palabra+" Es instrucción del modo relativo");
                    newLine=REL.RevisarLinea(line, m, VCE, numMemoria, 1, numLinea, inicio);
                    break;
                }
        }
        return newLine;
    }

    public int EsInstruccion(String palabra, Mnemonicos m) {
        int op;
        
        //Se recuperan las tablas de todos los modos de direccionamiento 
        //System.out.println("La palabra a diferenciar es:"+palabra);
        
        Hashtable<String, String> Inmediato;
        Hashtable<String,String> Inherente;
        Hashtable<String, String> Relativo;
        Hashtable<String, String> IndexadoX;
        Hashtable<String, String> IndexadoY;
        Hashtable<String, String> Directo;
        Hashtable<String, String> Extendido;
        
        Inmediato = m.LeerOpcode("ListaInmediato.txt");
        Relativo = m.LeerOpcode("ListaRelativo.txt");
        Inherente = m.LeerOpcode("ListaInherente.txt");
        IndexadoX= m.LeerOpcode("ListaIndexadoX.txt");
        IndexadoY=m.LeerOpcode("ListaIndexadoY.txt");
        Directo=m.LeerOpcode("ListaDirecto.txt");
        Extendido=m.LeerOpcode("ListaExtendido.txt");
        
        if(Directo.containsKey(palabra)||Inmediato.containsKey(palabra)||Extendido.containsKey(palabra)){
            op=1;
        }else if(Inherente.containsKey(palabra)){
            op=2;
        }else if(Relativo.containsKey(palabra)){
            op=3;
        }else{
            op=0;
        }
        return op;
        //Comprobamos si la palabra corresponde a algúna instrucción 
         
    }
    
    public String GuardarVariables(String line, Hashtable<String,String> variables){
       //Palabra nos sirve para separar la linea en palabras y contabilizarlas
                    String palabra, clave="";
                    String valor="";
                    int numPalabra=0;
                    String aux="";

                    //Se leen las palabras de la línea
                    StringTokenizer st1 = new StringTokenizer (line);
                    while (st1.hasMoreTokens())
                    {
                        palabra = st1.nextToken();
                        numPalabra++;
                        aux="";

                        /*Guarda el nombre de la variable o etiqueta*/
                        if(numPalabra==1){
                            clave=palabra=palabra.toUpperCase(); 
                        }

                        //Guarda el contenido de la variable, quitando el $ en el proceso 
                        if((numPalabra==3)){
                            String Saux=palabra.substring(1);
                            if(Saux.length()==2||Saux.length()==4){
                               valor=Saux; 
                            }else{
                                System.out.println("\u001B[31m Error: LONGITUD DE VARIABLE INCORRECTA\u001B[0m");
                                return line+"\n\t\t\t^Error: LONGITUD DE VARIABLE INCORRECTA";
                            }
                           
                        }
                    }
                    //Envia a la funcion para guardar en la HashTable, y revisar que no contenga
                    //otra constante o variable con el mismo nombre
                    boolean error = GuardarVariablesH(clave,valor,variables);
                    if (error){
                        String mensaje = line + "\u001B[31m\n\t\t\t^Error: Variable o constante repetida.\u001B[0m\n";
                        //Guardamos la salida de la primer pasada
                        Output outPut = new Output();
                        outPut.mensaje = mensaje;
                        metodosDeLectura.salidas.add(outPut);
                        return line + "\n\t\t\t^Error: Variable o constante repetida.";
                    }else{
                        String mensaje = "\033[0;1m"+valor+"          "+"\u001B[0m"+line;
                        //Guardamos la salida de la primer pasada
                        Output outPut = new Output();
                        outPut.mensaje = mensaje;
                        metodosDeLectura.salidas.add(outPut); 
                    }
                return valor+"          "+line;
}
    
    public boolean GuardarVariablesH(String clave, String valor, Hashtable <String,String> variables){
        
        
        if(variables.containsKey(clave)){
           
            return true;
            
        }else{
            variables.put(clave,valor);
            System.out.println("---Tabla var");
            System.out.println(variables);
        }
       
        return false;
        
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
