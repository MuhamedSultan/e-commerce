package com.example.e_commerce_app.db

import androidx.room.TypeConverter
import com.example.e_commerce_app.model.product.Image
import com.example.e_commerce_app.model.product.Option
import com.example.e_commerce_app.model.product.Variant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductTypeConverters {

    private val gson = Gson()

    // For List<Image>
    @TypeConverter
    fun fromImageList(value: List<Image>?): String? {
        val type = object : TypeToken<List<Image>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toImageList(value: String?): List<Image>? {
        val type = object : TypeToken<List<Image>>() {}.type
        return gson.fromJson(value, type)
    }

    // For Image
    @TypeConverter
    fun fromImage(image: Image?): String? {
        return gson.toJson(image)
    }

    @TypeConverter
    fun toImage(imageString: String?): Image? {
        return gson.fromJson(imageString, Image::class.java)
    }

    // For List<Option>
    @TypeConverter
    fun fromOptionList(value: List<Option>?): String? {
        val type = object : TypeToken<List<Option>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOptionList(value: String?): List<Option>? {
        val type = object : TypeToken<List<Option>>() {}.type
        return gson.fromJson(value, type)
    }

    // For List<Variant>
    @TypeConverter
    fun fromVariantList(value: List<Variant>?): String? {
        val type = object : TypeToken<List<Variant>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toVariantList(value: String?): List<Variant>? {
        val type = object : TypeToken<List<Variant>>() {}.type
        return gson.fromJson(value, type)
    }


    // Converter for template_suffix
    @TypeConverter
    fun fromTemplateSuffix(value: Any?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTemplateSuffix(value: String?): Any? {
        return gson.fromJson(value, Any::class.java)
    }

}
