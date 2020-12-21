package andrey.myappcompany.dzguessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private ImageView imageViewStar;
    private String url = "https://www.forbes.ru/rating/403469-40-samyh-uspeshnyh-zvezd-rossii-do-40-let-reyting-forbes";

    //данные которые получаем из поток о ссылке на картинку и имен людей надо поместить в массивы, а для этого сперва создать их
    private ArrayList<String> urls;
    private ArrayList<String> names;
    //после создания даем им назначение в onCreate

    //создаю переменные для play game и generate question
    private int numberOfQuestion;
    private int numberOfRightAnswer;
    private ArrayList<Button> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        buttons = new ArrayList<>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);


        imageViewStar = findViewById(R.id.imageViewStar);
        //присваиваю значение массивам ссылок на картинку и имён
        urls = new ArrayList<>();
        names = new ArrayList<>();
        getContent();
        playGame();

    }

    //Этап 0 - создаём в коде проекцию кнопок и рамки для картинок + присваиваем им значения в oncreate
    //Этап 1 - создать два класса для загрузки контента + загрузки изображения
    //-Реализуем метод doinbackground, создаем в нем элементы - url, httpurlconnection и stringbuilder, далее новый url - берем значение из параметра, ссылки типа
    //-Новый url в try catch оборачиваем
    //-Открываем соединение urlopenconnection, добавляем необходимый catch
    //-В finally если соединение не равно null, мы его закрываем
    //-Получаем inputstream - поток ввода, из url connection
    //-Создаём ридер inputstream
    //-Создаём буффер ридер, чтобы читать данные построчно
    //-Начинаем читать построчно, до тех пор
    //...
    //-После того как классы созданы, создается метод getContent с помощью которого из первого DownloadContentTask получаю контент
    //тк могут быть ошибки добавляется try - catch
    //тестово данные сперва выводятся в консоль (потом сплит контент выводим)
    //метод getContent вызывается в onCreate
    //Конец этапа 1
    //после этого создаю методы play game b generate question для него

    private void getContent(){
        DownloadContentTask task = new DownloadContentTask();
        try {
            String content = task.execute(url).get();
            String start = "<div class=\"title\">Топ 10</div>";
            String finish = "<div class=\"title\">Весь рейтинг</div>";
            Pattern pattern = Pattern.compile(start + "(.*?)" + finish);
            Matcher matcher = pattern.matcher(content);
            //цикл по условию *пока находятся совпадения*
            String splitContent = "";
            while (matcher.find()) {
                splitContent = matcher.group(1);
            }
            Pattern patternImg = Pattern.compile("<img src=\"(.*?)\""); //это паттерн, чтобы получить адрес картинки (ссылку)
            Pattern patternName = Pattern.compile("<div class=\"item_title\">(.*?)</div>");
            Matcher matcherImg = patternImg.matcher(splitContent);
            Matcher matcherName = patternName.matcher(splitContent);
            //теперь эти данные надо поместить в массивы, а для этого сперва создать их (см выше) - после того как массивы созданы начинаю их заполнять - 1 метод для ссылок + 1 метод для имён
            while (matcherImg.find()) {
                urls.add(matcherImg.group(1));
            }

            while (matcherName.find()) {
                names.add(matcherName.group(1));
            }
            //в цикле выводятся названия - для проверки вывожу в консоль данные, чтобы убедиться что они скачиватся
/*            for (String s: names){
                Log.i("MyResult", s);
            }
            for (String l: urls){
                Log.i("MyLinks", l);
            }*/

           //Log.i("MyResult", content);
            Log.i("MyResult", splitContent);
            //теперь нужно получить из строки 2 массива: в одном будут храниться имена, во втором адреса картинок (ссылки) - для этого нужно 2 паттерна
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //этап 2 - создаю методы playgame и genearatequestion
    //шаг 1 - создать нужные int переменные (см выше)
    //шаг 2 - добавить все кнопки в один массив (см выше) + присвоить ему значения в методе onCreate (нужно для того, чтобы генерировать правильный вариант)
    //шаг 3 - в playgame вызываю метод generatequestion (чтобы генерировать вопрос и правильный ответ)
    //шаг 4 - в playgame вызываю downloadimagetask (чтобы в зависимости от сгенерированного номера получить картинку)
    //шаг 5 - в битмап перемещаю картинку + оборачиваю это в try - catch (по правилам)
    //шаг 6 - далее в try проверяю, что картинка не равно null и устанавливаю ее в imageview (чтобы отобразить на экране)
    //шаг 7 - (чтобы установить нужный текст на кнопок, на одной из которых должен быть правильный текст)


    private void playGame(){
        generateQuestion();
        DownloadImageTask task = new DownloadImageTask();
        try {
            Bitmap bitmap = task.execute(urls.get(numberOfQuestion)).get();
            if (bitmap != null){
                imageViewStar.setImageBitmap(bitmap);
                //***см коммент у этапа 3
                for (int i = 0; i < buttons.size(); i++) {
                    if (i == numberOfRightAnswer) {
                        buttons.get(i).setText(names.get(numberOfQuestion));
                    } else {
                        int wrongAnswer = generateWrongAnswer();
                        buttons.get(i).setText(names.get(wrongAnswer));
                    }
                }

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void generateQuestion(){
        numberOfQuestion = (int) (Math.random() * names.size());
        numberOfRightAnswer = (int) (Math.random() * buttons.size());
    }

    //Этап 3 - создать метод, генерирующий номера неправильных ответов
    //после в playgame надо создать цикл, чтобы устанавливать текст у кнопок в методе playgame***
    //После метод playgame вызывается в onCreate
    private int generateWrongAnswer(){
        return (int) (Math.random() * names.size());
    }

    //Последний этап - отображение в тосте результата (правильно/не правильно) как итог игры
    //шаг 1 - добавить в метод получение тэга нажатой кнопки (чтобы сравнить ее с правильным ответом и показать результат)
    //шаг 2 - добавить if else проверку номера кнопки тэга и правильного ответа (чтобы тост вывести нужный по смыслу)
    // тк тэг является типом String, его надо сперва преобразовать в число
    //метод play game переносится вниз, чтобы пользователь сперва видел, правильный у него ответ (при нажатии) или нет, иначе со след. картинкой сравнение идёт
    public void onClickAnswer(View view) {
        //playGame();
        Button button = (Button) view;
        String tag = button.getTag().toString();
        if (Integer.parseInt(tag) == numberOfRightAnswer) {
            Toast.makeText(this, "Верно!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неверно, правильный ответ - " + names.get(numberOfQuestion), Toast.LENGTH_SHORT).show();
        }
        playGame();
    }

    //принимает стринг, отображает войд и передаёт текст. все программирование это обмен данными
    private static class DownloadContentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings){
            URL url = null;
            HttpURLConnection httpURLConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null){
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    //принимает стринг, отображает войд и передаёт картинку битмап. все программирование это обмен данными
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection httpURLConnection = null;
         //   StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                //здесь из нашего inputstream создаём bitmap, поэтому стрингбилдер не нужен
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }
}
