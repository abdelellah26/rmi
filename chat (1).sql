-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : sam. 25 mai 2024 à 13:31
-- Version du serveur : 10.4.28-MariaDB
-- Version de PHP : 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `chat`
--

-- --------------------------------------------------------

--
-- Structure de la table `blocked_users`
--

CREATE TABLE `blocked_users` (
  `id` bigint(20) NOT NULL,
  `blocker_id` bigint(20) NOT NULL,
  `blocked_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `blocked_users`
--

INSERT INTO `blocked_users` (`id`, `blocker_id`, `blocked_id`) VALUES
(3, 5, 2),
(4, 6, 5);

-- --------------------------------------------------------

--
-- Structure de la table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `message` text NOT NULL,
  `id_sender` bigint(20) NOT NULL,
  `id_resever` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `messages`
--

INSERT INTO `messages` (`id`, `message`, `id_sender`, `id_resever`) VALUES
(70, 'hi', 2, 5),
(71, 'hhhh', 2, 5),
(72, 'ttttt', 5, 2),
(73, 'hello fadli', 2, 6),
(74, 'hiiii', 6, 2);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`) VALUES
(2, 'abdelellah issile', 'issile@gmail.com', '123456'),
(3, 'issile', 'issile1@gmail.com', '123456'),
(4, 'abdelellah issile', 'issile2@gmail.com', '123456'),
(5, 'khalid', 'khalid@gmail.com', '123456'),
(6, 'mohammed', 'fadli@gmail.com', '123456');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `blocked_users`
--
ALTER TABLE `blocked_users`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_resever` (`id_resever`),
  ADD KEY `id_sender` (`id_sender`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `blocked_users`
--
ALTER TABLE `blocked_users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`id_resever`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`id_sender`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
