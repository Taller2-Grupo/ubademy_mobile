package com.ubademy_mobile.activities.tools

import java.text.Normalizer


class HashtagManager {

    private var hashtags = mutableListOf<String>()

    fun validateHashtag( new_hashtag : String): Boolean{

        val new_hashtag_norm = normalize(new_hashtag)
        if (hashtags.contains(new_hashtag_norm)){
            return false
        }

        val regex = Regex("[^#A-Z0-9]")
        if(new_hashtag_norm.contains(regex)){
            return false
        }

        if(new_hashtag_norm.isEmpty()){
            return false
        }

        return true
    }

    fun normalize(new_hashtag: String) : String{

        var normalized = new_hashtag.uppercase()

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")

        normalized = normalized.replace(" ","")

        if ( new_hashtag.isNotEmpty() && new_hashtag[0] == '#')
            normalized = normalized.replace("#","")


        return normalized
    }

    fun addHashtag(hashtag: String) {
        hashtags.add(normalize(hashtag))
    }

    fun removeHashtag(hashtag: String){
        hashtags.remove(hashtag)
    }

    fun setHashtags( new_hashtags : MutableList<String> ){
        hashtags = new_hashtags
    }

    fun getHashtags() : MutableList<String> {
        return hashtags
    }

}