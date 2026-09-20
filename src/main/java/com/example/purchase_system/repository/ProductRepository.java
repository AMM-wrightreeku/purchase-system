package com.example.purchase_system.repository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.purchase_system.entity.Product;
import com.example.purchase_system.util.CsvFileUtil;
import com.example.purchase_system.util.IdGenerator;


@Repository
public class ProductRepository {
    private Map<Integer, Product> products;

    public ProductRepository() {
        products = new LinkedHashMap<>();
        loadProductFromCsv();
    }

    public Product save(String barcode, String name) {
        // check
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank."); // 空白名字 
        }
        if (barcode != null && findByBarcode(barcode).isPresent()) {
            throw new IllegalArgumentException("Barcode already exists"); // 條碼已經存在
        }
        // main motion
        int id = IdGenerator.getNextId(products.keySet()); // 呼叫 util 直接找 ID
        Product product = new Product(id, barcode, name);
        saveProductToCsv(product);
        products.put(id, product);
        return product;
    }
    // find
    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }
    public Optional<Product> findByBarcode(String barcode) {
        if(barcode == null || barcode.isBlank()) return Optional.empty();

        for (Product product : products.values()) {
            if (barcode.equals(product.getBarcode())) {
                return Optional.of(product);
            }
        }
        
        return Optional.empty();
    }
    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        
        if (name == null || name.isBlank()) return result;
        
        for (Product product : products.values()) {
            if (product.getName().contains(name)) {
                result.add(product);
            }
        }

        return result;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }   

    private void saveProductToCsv(Product product) {
        Path path = Path.of("data", "productList.csv");
        try {
            String line = "CREATE," 
                            + product.getId() + "," 
                            + ((product.getBarcode() == null) ? "" : product.getBarcode()) + "," 
                            + product.getName() + System.lineSeparator();
            Files.writeString(path, line, StandardCharsets.UTF_8
                                , StandardOpenOption.APPEND
            );
        } catch(IOException e) {
            throw new IllegalStateException("Cannot save productList.csv", e);
        }
    }

    private void loadProductFromCsv() {
        try {
            List<String> lines = CsvFileUtil.loadLines("data", "productList.csv"
                                                        , "STATUS,ID,BARCODE,NAME");
            for(int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.isBlank()){continue;} // 我們決定採用 append 但以防萬一還是保留
                String[] columns = line.split(",", 4);
                // CREATE, ID, BARCODE, NAME
                if(columns.length < 4) {throw new IllegalArgumentException(
                    "productList.csv format error at line " + (i+1));
                }
                String status = columns[0].trim();
                if(!"CREATE".equals(status)) { continue;}
                int id = Integer.parseInt(columns[1].trim());
                String barcode = columns[2].trim();
                String name = columns[3].trim();
                Product product = new Product(id, barcode, name);
                products.put(id, product);
            }
        } catch(IOException e) {
            throw new IllegalStateException("Cannot read productList.csv", e);
        }
    }
}