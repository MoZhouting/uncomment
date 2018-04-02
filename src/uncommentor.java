import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class uncommentor {


    public uncommentor() throws IOException {
    }

    public static void main(String args[]) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        uncommentor a = new uncommentor();
        String inputFilename = "D:\\1.txt";
        String outputFilename = "D:\\2.txt";
        a.writeFile (outputFilename);
        List<String> content = a.readFile(inputFilename);
    }

    public List<String> readFile (String filename){
        File file = new File(filename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            setState("normal");
            while((line = br.readLine())!=null){//使用readLine方法，一次读一行
                uncommentLine2(line);
                bw.write("\r\n");

            }
            br.close();
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        List<String> lines = new ArrayList<String>();
////        lines.add("a//1\n");
////        lines.add("\"c//2\"\n");
////        lines.add("a=\"\\\"123\",b='\"';//456\n");
//        lines.add("abc;/*bb\n");
//        lines.add("abc;bb\n");
//        lines.add("abc;b*/bb\n");
//
        return lines;
    }


     public String uncommentLine2(String line) {


        for (int i = 0; i < line.length(); i++) {
            char now = line.charAt(i);
            try {
                currentState.handle(now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void setState(String name) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(!map.containsKey(name)){

            String className = map2.get(name);
            if (className == null) {
                System.out.println(name);
            }
            Class clz = Class.forName(className);
            IState state = (IState)clz.getDeclaredConstructor(new Class[]{uncommentor.class}).newInstance(this);
            map.put(name, state);
        }
        currentState = map.get(name);
    }

    private void stateMap(String statename){
        if(!map.containsKey(statename)) {
            SlashState slashState = new SlashState();
            map.put(statename,slashState);
        }
        currentState = map.get(statename);
    }


    public void writeFile (String path) throws IOException {
        File file = new File(path);
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
    }

    interface IState {
        void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException;
    }

    class NormalState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            if (ch == '/') {
                setState("slash");
            }
            else if (ch == '"') {
                setState("quotation");
                bw.write(ch);
                //System.out.print(ch);
            }
            else if (ch == '\'') {
                setState("single_quotation");
                bw.write(ch);
            }
            else {
                bw.write(ch);
            }

        }
    }

    class SlashState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            if (ch == '/') {
                setState("double_slash");
            }
            else if (ch == '*') {
                setState("slash_star");
            }
            else {
                setState("normal");
                bw.write("/" +ch);
            }

        }
    }

    class QuotationState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            if (ch == '\\') {
                setState("quotation_backslash");
                bw.write(ch);

            }
            else if (ch == '"') {
                setState("normal");
                bw.write(ch);
            }
            else {
                setState("quotation");
                bw.write(ch);
            }
        }
    }

    class DoubleSlashState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            if (ch == '\n') {
                setState("normal");
                bw.write("\n");

            }
        }
    }


    class SlashStarState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            if (ch == '*') {
                setState("slash_doubleStar");
            }
            if (ch == '\n') {
                bw.write("\n");
            }
        }
    }

    class QuotationBackslashState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            bw.write(ch);
        }
    }

    class SlashDoubleStarState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            if (ch == '/') {
                setState("normal");
            }
            else {
                setState("slash_star");
            }
        }
    }

    class SingleQuotationState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            if (ch == '\\') {
                setState("singleQuotation_backSlash");
                bw.write(ch);
            }
            else {
                setState("singleQuotation_other");
                bw.write(ch);
            }
        }
    }

    class SingleQuotationOtherState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            setState("normal");
            bw.write(ch);
        }
    }

    class SingleQuotationBackSlashState implements IState {
        @Override
        public void handle(char ch) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            setState("singleQuotation_other");
            bw.write(ch);
        }
    }

//    private int state = -1;
//    private static final int STATE_NORMAL = 1;
//    private static final int STATE_SLASH = 2;
//    private static final int STATE_QUOTATION = 3;
//    private static final int STATE_DOUBLESLASH = 4;
//    private static final int STATE_SLASH_STAR = 5;
//    private static final int STATE_QUOTATION_BACKSLASH = 6;
//    private static final int STATE_SLASH_DOUBLESTAR = 7;
//    private static final int STATE_SINGLEQUOTATION = 8;
//    private static final int STATE_SINGLEQUOTATION_OTHER = 9;
//    private static final int STATE_SINGLEQUOTATION_BACKSLASH = 10;

    private Map<String,IState> map=new HashMap<String,IState>();
    private IState currentState = null;
    private static final Map<String, String> map2 = new HashMap<>();
    private BufferedWriter bw = null;

    static {
        map2.put("normal", "uncommentor$NormalState");
        map2.put("slash", "uncommentor$SlashState");
        map2.put("quotation", "uncommentor$QuotationState");
        map2.put("double_slash", "uncommentor$DoubleSlashState");
        map2.put("slash_star", "uncommentor$SlashStarState");
        map2.put("quotation_backslash", "uncommentor$QuotationBackslashState");
        map2.put("slash_doubleStar", "uncommentor$SlashDoubleStarState");
        map2.put("singleQuotation", "uncommentor$SingleQuotationState");
        map2.put("singleQuotation_other", "uncommentor$SingleQuotationOtherState");
        map2.put("singleQuotation_backSlash", "uncommentor$SingleQuotationBackSlashState");
    }
}



