package br.univates.service;

import br.univates.model.recipes;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public ByteArrayInputStream generate(List<recipes> recipes) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Relatório de Receitas", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // Tabela
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 5, 2, 2});

            addHeader(table, "Nome");
            addHeader(table, "Descrição");
            addHeader(table, "Preço");
            addHeader(table, "Tipo");

            for (recipes r : recipes) {
                table.addCell(r.getName());
                table.addCell(r.getDescription());
                table.addCell(r.getPrice().toString());
                table.addCell(r.getRecipe_type());
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void addHeader(PdfPTable table, String text) {
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        PdfPCell hcell = new PdfPCell(new Phrase(text, headFont));
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(hcell);
    }
}