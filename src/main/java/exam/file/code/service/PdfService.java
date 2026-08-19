package exam.file.code.service;

import static java.io.File.createTempFile;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import exam.file.code.Entity.Note;
import exam.file.code.Entity.Student;
import java.io.File;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

  @SneakyThrows
  public File generateReleveDeNotes(Student student, List<Note> notes) {
    var filePrefix = "releve-" + student.getId();
    var fileSuffix = ".pdf";
    File file = createTempFile(filePrefix, fileSuffix);

    try (PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc)) {

      document.add(new Paragraph("Relevé de notes").setBold().setFontSize(18));
      document.add(
          new Paragraph(student.getUsername() != null ? student.getUsername() : student.getId())
              .setFontSize(12));

      Table table = new Table(UnitValue.createPercentArray(new float[] {3, 2}));
      table.setWidth(UnitValue.createPercentValue(100));
      table.addHeaderCell("Examen");
      table.addHeaderCell("Note");

      for (Note note : notes) {
        table.addCell(note.getExamen().getName());
        table.addCell(String.valueOf(note.getValeur()));
      }

      document.add(table);
    }

    return file;
  }
}
