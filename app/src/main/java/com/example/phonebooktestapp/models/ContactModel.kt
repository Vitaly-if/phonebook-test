package com.example.phonebooktestapp.models

import android.os.Parcel
import android.os.Parcelable

class ContactModel : Parcelable {
    var contactId: Long = 0

    var contactAvatarImg: String = ""

    var name: String = ""

    var phone: String = ""

    var groupID: Long = 0L

    constructor(parcel: Parcel) {
        contactId = parcel.readLong()
        contactAvatarImg = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
        groupID = parcel.readLong()
    }

    constructor(id: Long, address: String, name: String, phone: String, group: Long) {
        this.contactId = id
        this.contactAvatarImg = address
        this.name = name
        this.phone = phone
        this.groupID = group
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(contactId)
        parcel.writeString(contactAvatarImg)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeLong(groupID)

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