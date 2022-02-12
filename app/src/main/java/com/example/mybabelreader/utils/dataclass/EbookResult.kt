package com.example.mybabelreader.utils.dataclass


sealed class EbookResult{
    object SUCCEEDED: EbookResult()
    data class FAILED(val msg: String): EbookResult()
}
