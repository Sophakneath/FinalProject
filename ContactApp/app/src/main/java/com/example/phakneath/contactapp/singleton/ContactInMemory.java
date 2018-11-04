package com.example.phakneath.contactapp.singleton;

import com.example.phakneath.contactapp.model.ContactInPhone;

import java.util.ArrayList;
import java.util.List;

public class ContactInMemory {

    List<ContactInPhone> contactInPhones;
    public ContactInMemory()
    {
        contactInPhones = new ArrayList<>();
    }

    public void saveUsers(ContactInPhone user)
    {
        contactInPhones.add(user);
    }

    public List<ContactInPhone> getUsers()
    {
        return contactInPhones;
    }
}
