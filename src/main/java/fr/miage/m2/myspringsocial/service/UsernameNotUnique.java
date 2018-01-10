package fr.miage.m2.myspringsocial.service;

public class UsernameNotUnique extends Exception {

  public UsernameNotUnique(String message) {
    super(message);
  }
}
