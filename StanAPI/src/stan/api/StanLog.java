package stan.api;

import java.util.Date;

public class StanLog
{
//Поля
    static public String[] results = (
            "[ >OK< ]"//0
            +"\t"+
            "[ WTF O_o ]"//1
            +"\t"+
            "[ warning ]"//2
            +"\t"+
            "[ info ]"//3
            ).split("\t");
    
//Методы
    //добавление строки с подписью в отладочный лог
    static public  void add_log(int n, String u, String s)
    {
        String text = "[" + Main.date_to_string(new Date())  + "]" + " " +results[n]+ " " + "[" +u+ "]" + " "  + s;
        System.out.println(text);
    }
    
//Конструкторы
}