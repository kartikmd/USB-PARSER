# USB PD PDF ToC Parser — Spring Boot Backend

A minimal Spring Boot API that accepts a USB PD PDF, extracts its **Table of Contents** (ToC) from the front matter, and returns a **JSONL** stream (`usb_pd_spec.jsonl`) with entries like:

```
{"doc_title":"USB PD Specification","section_id":"2.1.2","title":"Power Delivery Contract Negotiation","page":53,"level":3,"parent_id":"2.1","full_path":"2.1.2 Power Delivery Contract Negotiation"}
```

## How it works
- Uses **Apache PDFBox** to extract text from the first N pages (configurable).
- Parses likely ToC lines using robust regex + “last number is page” heuristic.
- Computes `level` from number of dots in `section_id`, and `parent_id` by trimming the last segment.
- Streams JSONL back as a file download, or you can redirect it to disk.

## Build & Run
```bash
# Java 17 + Maven required
mvn spring-boot:run
# or build a jar
mvn -q -DskipTests package && java -jar target/usbpd-parser-springboot-0.0.1-SNAPSHOT.jar
```

## API
### `POST /api/v1/parse-toc`
Multipart request:

- **file**: the PDF to parse
- **docTitle** *(optional)*: defaults to file name
- **frontPages** *(optional)*: how many initial pages to scan for ToC (default: 15)

Returns: `application/jsonl` file (`usb_pd_spec.jsonl`) as attachment.

Example:
```bash
curl -X POST "http://localhost:8080/api/v1/parse-toc"   -F "file=@/path/to/usbpd.pdf"   -F "docTitle=USB PD Spec Rev 3.x"   -F "frontPages=18"   -o usb_pd_spec.jsonl
```

## Notes
- TOC formats vary; adjust regexes in `TocParserService` if your spec uses unusual leaders (e.g., dot leaders, tabs).
- If your ToC doesn’t have explicit section IDs, consider adding a fuzzy heading detector (out of scope for this MVP).
