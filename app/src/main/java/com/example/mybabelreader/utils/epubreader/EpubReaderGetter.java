package com.example.mybabelreader.utils.epubreader;

import com.example.mybabelreader.model.ReaderActivityViewModel;
import com.example.mybabelreader.utils.dataclass.EbookResult;

import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class EpubReaderGetter {
    public static ReaderActivityViewModel.EbookData buildEbook(InputStream inputStream) {
        if (inputStream == null) {
            return new ReaderActivityViewModel.EbookData("The ebook given is empty", null);
        }
        try {
            Book epubRaw = new EpubReader().readEpub(inputStream);
            if (epubRaw.getContents().isEmpty()) {
                return new ReaderActivityViewModel.EbookData("The book is empty", null);
            } else {
                return new ReaderActivityViewModel.EbookData(null, EbookReader.initializeBook(epubRaw));
            }
        } catch (Exception e) {
            return new ReaderActivityViewModel.EbookData(e.toString(), null);
        }
    }
}


