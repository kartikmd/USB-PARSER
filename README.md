Got it 👍 Here’s a **README.md** file you can use for your assignment.
I’ll keep it simple, clear, and matching your deliverables.

---

# 📘 USB Power Delivery (USB PD) Specification Parser

## 📌 Project Overview

This project extracts the **Table of Contents (ToC)** from a **USB Power Delivery (USB PD) Specification PDF** and converts it into a **structured JSONL file**.
The JSONL output preserves the **hierarchy of sections** (chapter → section → subsection) along with metadata such as section IDs, titles, page numbers, and parent-child relationships.

The structured data can be used for:

* Document search
* Knowledge graph creation
* Validation and analysis of USB PD specifications

---

## 🚀 Features

* Extracts Table of Contents (ToC) from a PDF
* Parses each entry into:

  * `section_id` (e.g., `2.1.2`)
  * `title` (e.g., *Power Delivery Contract Negotiation*)
  * `page` (page number)
  * `level` (hierarchy depth)
  * `parent_id` (e.g., `2.1` is parent of `2.1.2`)
  * `full_path` (section + title)
  * `doc_title` (document title)
* Outputs **JSONL format** (one JSON object per line)
* Handles malformed lines gracefully

---

## 🛠️ Tech Stack

* **Language**: Python 3.9+
* **Libraries**:

  * [pdfplumber](https://github.com/jsvine/pdfplumber) / [PyMuPDF](https://pymupdf.readthedocs.io/) → PDF text extraction
  * `re` → regex parsing
  * `json` → JSON output

---

## 📂 Project Structure

```
.
├── usb_pd_parser.py       # Main script
├── usb_pd_spec.jsonl      # Output JSONL file
├── README.md              # Documentation
└── usb_pd_spec.pdf        # Input PDF (USB PD Specification)
```

---

## ⚡ How to Run

1. **Install dependencies**

   ```bash
   pip install pdfplumber pymupdf
   ```

2. **Place the PDF file** in the project folder (e.g., `usb_pd_spec.pdf`).

3. **Run the parser**

   ```bash
   python usb_pd_parser.py usb_pd_spec.pdf
   ```

4. **Check output** → `usb_pd_spec.jsonl` will be created with structured ToC entries.

---

## 📄 Sample Output (JSONL)

```json
{"doc_title": "USB Power Delivery Specification Rev X", "section_id": "2", "title": "Overview", "page": 53, "level": 1, "parent_id": null, "full_path": "2 Overview"}
{"doc_title": "USB Power Delivery Specification Rev X", "section_id": "2.1", "title": "Introduction", "page": 53, "level": 2, "parent_id": "2", "full_path": "2.1 Introduction"}
{"doc_title": "USB Power Delivery Specification Rev X", "section_id": "2.1.2", "title": "Power Delivery Contract Negotiation", "page": 54, "level": 3, "parent_id": "2.1", "full_path": "2.1.2 Power Delivery Contract Negotiation"}
```

---

## ✅ Deliverables

* `usb_pd_parser.py` → Python script
* `usb_pd_spec.jsonl` → Parsed output file
* `README.md` → Documentation (this file)

---

✨ Done!
This README is short, professional, and exactly what your evaluator expects.

Do you want me to also make a **README.txt version** (since sometimes assignments don’t require `.md` formatting)?
