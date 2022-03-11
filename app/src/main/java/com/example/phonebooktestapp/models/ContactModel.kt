package com.example.phonebooktestapp.models

import android.os.Parcel
import android.os.Parcelable

class ContactModel : Parcelable {
    var contactId: Long = 0;

    var contactAvatarImg: String = ""
        get() = field
        set(value) {
            field = value
        }

    var name: String = ""
        get() = field
        set(value) {
            field = value
        }

    var phone: String = ""
        get() = field
        set(value) {
            field = value
        }

    var category: String = ""
        get() = field
        set(value) {
            field = value
        }

    constructor(parcel: Parcel) {
        contactId = parcel.readLong()
        contactAvatarImg = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        category = parcel.readString().toString()
    }


    constructor(id: Long, address: String, name: String, phone: String, group: String) {
        this.contactId = id;
        this.contactAvatarImg = address
        this.name = name
        this.phone = phone
        this.category = group
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(contactId)
        parcel.writeString(contactAvatarImg)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(category)

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactModel

        if (contactId != other.contactId) return false

        return true
    }

    override fun hashCode(): Int {
        return contactId.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<ContactModel> {
        override fun createFromParcel(parcel: Parcel): ContactModel {
            return ContactModel(parcel)
        }

        override fun newArray(size: Int): Array<ContactModel?> {
            return arrayOfNulls(size)
        }
    }
}