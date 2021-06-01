package com.mikerusoft.citizens.infra

case class EnvMonad[E, A](run: E => A) {

  def map[B](func: A => B): EnvMonad[E, B] = EnvMonad(e => func(run(e)))

  def flatMap[B](func: A => EnvMonad[E, B]): EnvMonad[E, B] = EnvMonad(e => func(run(e)).run(e))

}
