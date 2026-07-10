package com.product_catalog_write_service.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import com.common.avro.schemas.ProductEvent;

public class AvroSerializerUtil {

    public static byte[] serializeProductPayload(ProductEvent payload) {
        SpecificDatumWriter<ProductEvent> datumWriter = 
                new SpecificDatumWriter<>(ProductEvent.class);
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            datumWriter.write(payload, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize Avro payload for product ID: " + payload.getId(), e);
        }
    }
}