package fr.miage.m2.myspringsocial.account;

public class UsernameNotUnique extends Exception {

  UsernameNotUnique(String message) {
    super(message);
  }
}
