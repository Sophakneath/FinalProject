package com.example.phakneath.contactapp.singleton;

public class ContactInPhoneObj {

    private static ContactInMemory repository;
    public static ContactInMemory getRepository()
    {
        if (repository == null)
        {
            repository = new ContactInMemory();

        }
        return repository;
    }

}
