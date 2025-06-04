package com.peluqueria.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
import com.peluqueria.entity.Boleta;
import com.peluqueria.entity.Cita;
import com.peluqueria.entity.DetalleBoleta;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PDFService {

    public byte[] generarComprobantePDF(Cita cita) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            document.add(new Paragraph("COMPROBANTE DE CITA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold());

            document.add(new Paragraph("\n"));

            // Información de la cita
            document.add(new Paragraph("Código de Cita: " + cita.getCodigoUnico()).setBold());
            document.add(new Paragraph("Cliente: " + cita.getCliente().getNombreCompleto()));
            document.add(new Paragraph("Peluquero: " + cita.getPeluquero().getNombreCompleto()));
            document.add(new Paragraph("Fecha y Hora: " + cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph("Estado: " + cita.getEstado()));

            document.add(new Paragraph("\n"));

            // Información de la boleta
            if (cita.getBoleta() != null) {
                Boleta boleta = cita.getBoleta();
                document.add(new Paragraph("DETALLE DE SERVICIOS").setBold());

                // Tabla
                Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 2, 2, 2 }))
                        .useAllAvailableWidth();

                // Encabezados
                table.addHeaderCell(
                        new Cell().add(new Paragraph("Descripción")).setBold().setBorder(new SolidBorder(1)));
                table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBold().setBorder(new SolidBorder(1)));
                table.addHeaderCell(
                        new Cell().add(new Paragraph("Precio Unit.")).setBold().setBorder(new SolidBorder(1)));
                table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")).setBold().setBorder(new SolidBorder(1)));

                for (DetalleBoleta detalle : boleta.getDetalles()) {
                    table.addCell(
                            new Cell().add(new Paragraph(detalle.getDescripcion())).setBorder(new SolidBorder(0.5f)));
                    table.addCell(new Cell().add(new Paragraph(detalle.getCantidad().toString()))
                            .setBorder(new SolidBorder(0.5f)));
                    table.addCell(new Cell().add(new Paragraph("S/ " + detalle.getPrecioUnitario()))
                            .setBorder(new SolidBorder(0.5f)));
                    table.addCell(new Cell().add(new Paragraph("S/ " + detalle.getSubtotal()))
                            .setBorder(new SolidBorder(0.5f)));
                }

                document.add(table);

                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Subtotal: S/ " + boleta.getSubtotal()));
                document.add(new Paragraph("IGV (18%): S/ " + boleta.getIgv()));
                document.add(new Paragraph("TOTAL: S/ " + boleta.getTotal()).setBold());
            }

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("¡Gracias por su preferencia!")
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }
}
