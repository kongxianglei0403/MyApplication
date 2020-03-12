package com.kxl.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {

    public static final String DEST = "results/tables/a.pdf";
    private static final String fileName = "a.pdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initFile();
    }

    String path;
    File file;
    private void initFile() {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        /*Log.e("SecondActivity","创建" + file.exists());
        if (!file.exists()){
            boolean b = file.mkdir();
            Log.e("SecondActivity","创建" + b);
        }*/

    }

    public void translate(View view) {
        Document document = new Document();//横向打印
        try {
            PdfWriter.getInstance(document, new FileOutputStream(DEST));
            document.open();
            // 中文字体(现在高版本的不支持中文包)
//            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//            Font fontChinese = new Font(bfChinese, 12, Font.NORMAL);// 中文、12、正常
            int colNumber = 6;
            PdfPTable datatable = new PdfPTable(colNumber);//创建pdf表格
            // 定义表格的宽度
//            int[] cellsWidth = {1, 1, 1, 1, 1, 1};
//            datatable.setWidths(cellsWidth);// 单元格宽度
//            // datatable.setTotalWidth(300f);//表格的总宽度
//            datatable.setWidthPercentage(100);// 表格的宽度百分比
//            datatable.getDefaultCell().setPadding(2);// 单元格的间隔
//            datatable.getDefaultCell().setBorderWidth(2);// 边框宽度
//            // 设置表格的底色
//            datatable.getDefaultCell().setBackgroundColor(BaseColor.GREEN);
//            datatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            // 添加表头元素
             for (int i = 0; i < colNumber; i++) {
                datatable.addCell("fadfa");
             }
             document.add(datatable);
            Log.e("SecondActivity","执行完成");
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            document.close();
        }


    }
}
