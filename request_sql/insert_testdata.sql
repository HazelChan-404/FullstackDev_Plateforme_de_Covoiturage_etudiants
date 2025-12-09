-- Co-Voiturage (拼车系统) 测试数据插入
-- 创建时间: 2025-12-05
USE covoiturage_full_stack;
-- ============================================
-- 插入测试用户
-- ============================================
INSERT INTO users (email, password_hash, first_name, last_name, phone, bio, is_verified, average_rating_driver, average_rating_passenger, total_trips_driver, total_trips_passenger) VALUES
('jean.dupont@email.com', '$2y$10$abcdefghijklmnopqrstuv', 'Jean', 'Dupont', '0612345678', 'Conducteur expérimenté, aime discuter pendant les trajets.', TRUE, 4.8, 4.9, 45, 12),
('marie.martin@email.com', '$2y$10$bcdefghijklmnopqrstuv1', 'Marie', 'Martin', '0623456789', 'Préfère les trajets calmes, ponctuelle.', TRUE, 4.9, 4.7, 32, 28),
('pierre.bernard@email.com', '$2y$10$cdefghijklmnopqrstuv12', 'Pierre', 'Bernard', '0634567890', 'Étudiant, fait souvent Paris-Lyon.', TRUE, 4.5, 4.8, 18, 35),
('sophie.petit@email.com', '$2y$10$defghijklmnopqrstuv123', 'Sophie', 'Petit', '0645678901', 'Professionnelle, trajets réguliers.', TRUE, 4.7, 4.6, 28, 15),
('luc.robert@email.com', '$2y$10$efghijklmnopqrstuv1234', 'Luc', 'Robert', '0656789012', 'Nouveau sur la plateforme.', FALSE, 0, 0, 0, 0),
('alice.dubois@email.com', '$2y$10$fghijklmnopqrstuv12345', 'Alice', 'Dubois', '0667890123', 'Aime partager les frais d\'essence.', TRUE, 4.6, 4.5, 22, 19);

-- ============================================
-- 插入测试行程
-- ============================================
INSERT INTO trips (driver_id, departure_address, departure_city, departure_lat, departure_lng, arrival_address, arrival_city, arrival_lat, arrival_lng, departure_datetime, total_seats, available_seats, price_per_seat, status, description) VALUES
-- 活跃行程
(1, '25 Rue de la République', 'Paris', 48.866667, 2.333333, '15 Place Bellecour', 'Lyon', 45.757814, 4.832011, '2025-12-08 09:00:00', 3, 2, 25.00, 'active', 'Trajet direct, arrêt possible à Dijon.'),
(2, '10 Avenue des Champs-Élysées', 'Paris', 48.869982, 2.307998, '5 Rue de la Liberté', 'Marseille', 43.296482, 5.369780, '2025-12-09 14:30:00', 4, 4, 35.00, 'active', 'Voiture confortable, climatisation.'),
(3, '30 Cours Lafayette', 'Lyon', 45.761905, 4.836117, '8 Place du Capitole', 'Toulouse', 43.604652, 1.444209, '2025-12-10 08:00:00', 3, 1, 28.00, 'active', 'Départ tôt le matin, retour possible le dimanche.'),
(4, '12 Boulevard de la Victoire', 'Marseille', 43.296174, 5.370000, '45 Avenue Jean Médecin', 'Nice', 43.710173, 7.261953, '2025-12-11 16:00:00', 3, 3, 15.00, 'active', 'Trajet côtier, belle vue.'),
(1, '5 Rue Saint-Jacques', 'Paris', 48.850000, 2.345000, '22 Rue Nationale', 'Bordeaux', 44.837789, -0.579180, '2025-12-12 10:00:00', 4, 2, 40.00, 'active', 'Grand coffre, place pour bagages.'),

-- 已完成的行程
(2, '18 Rue Victor Hugo', 'Paris', 48.871000, 2.328000, '7 Place de la Comédie', 'Lyon', 45.763420, 4.834277, '2025-11-28 09:00:00', 3, 0, 25.00, 'completed', 'Trajet complété sans problème.'),
(3, '25 Avenue Foch', 'Lyon', 45.764043, 4.835659, '10 Rue Paradis', 'Marseille', 43.293950, 5.375000, '2025-11-25 15:00:00', 4, 0, 30.00, 'completed', NULL),

-- 已取消的行程
(4, '8 Rue de Rivoli', 'Paris', 48.856613, 2.355000, '12 Cours Mirabeau', 'Marseille', 43.526319, 5.447427, '2025-11-20 11:00:00', 3, 3, 32.00, 'cancelled', 'Annulé pour raisons personnelles.');

-- ============================================
-- 插入预订记录
-- ============================================
INSERT INTO bookings (trip_id, passenger_id, seats_booked, total_price, status, message_to_driver, payment_status) VALUES
-- 已确认的预订
(1, 3, 1, 25.00, 'confirmed', 'Bonjour, je serai à l\'heure. Merci!', 'paid'),
(3, 2, 2, 56.00, 'confirmed', 'Nous sommes deux, merci de confirmer.', 'paid'),
(5, 6, 2, 80.00, 'confirmed', NULL, 'paid'),

-- 待处理的预订
(2, 5, 1, 35.00, 'pending', 'Première fois, un peu nerveux :)', 'unpaid'),
(4, 1, 2, 30.00, 'pending', 'Possible de partir un peu plus tôt?', 'unpaid'),

-- 已完成的预订
(6, 1, 2, 50.00, 'completed', 'Super trajet!', 'paid'),
(6, 4, 1, 25.00, 'completed', NULL, 'paid'),
(7, 1, 2, 60.00, 'completed', 'Merci pour le trajet', 'paid'),

-- 已取消的预订
(8, 3, 1, 32.00, 'cancelled', 'Désolé, je ne peux plus venir.', 'refunded');

-- ============================================
-- 插入评价
-- ============================================
INSERT INTO reviews (booking_id, reviewer_id, reviewee_id, rating, comment) VALUES
-- Jean évalue Marie (conductrice du trajet 6)
(6, 1, 2, 5, 'Excellente conductrice! Très ponctuelle et agréable.'),
-- Sophie évalue Marie (conductrice du trajet 6)
(7, 4, 2, 5, 'Parfait, recommande fortement.'),
-- Marie évalue Jean (passager du trajet 6)
(6, 2, 1, 5, 'Passager sympathique et ponctuel.'),
-- Marie évalue Sophie (passager du trajet 6)
(7, 2, 4, 4, 'Bonne passagère, un peu en retard au départ.'),

-- Jean évalue Pierre (conducteur du trajet 7)
(8, 1, 3, 4, 'Bon conducteur, un peu rapide sur l\'autoroute.'),
-- Pierre évalue Jean (passager du trajet 7)
(8, 3, 1, 5, 'Passager idéal, conversation intéressante!');

-- ============================================
-- 插入消息
-- ============================================
INSERT INTO messages (booking_id, sender_id, receiver_id, content, is_read) VALUES
-- Conversation pour la réservation 1
(1, 3, 1, 'Bonjour, à quelle heure exacte pensez-vous arriver?', TRUE),
(1, 1, 3, 'Bonjour! Je pense arriver vers 13h30.', TRUE),
(1, 3, 1, 'Parfait, merci!', TRUE),

-- Conversation pour la réservation 4
(4, 5, 2, 'Bonjour, c\'est ma première réservation, comment ça marche?', TRUE),
(4, 2, 5, 'Bienvenue! C\'est très simple, on se retrouve au point de départ.', TRUE),
(4, 5, 2, 'D\'accord merci! Vous avez mon numéro si besoin.', FALSE),

-- Conversation pour la réservation 5
(5, 1, 4, 'Bonjour, est-il possible de partir à 15h30 au lieu de 16h?', TRUE),
(5, 4, 1, 'Laissez-moi vérifier, je vous confirme rapidement.', FALSE);

-- ============================================
-- 插入通知
-- ============================================
INSERT INTO notifications (user_id, type, title, content, is_read) VALUES
(1, 'booking_confirmed', 'Réservation confirmée', 'Votre réservation pour Paris → Lyon le 08/12 a été confirmée.', TRUE),
(2, 'new_booking', 'Nouvelle réservation', 'Vous avez une nouvelle demande de réservation pour votre trajet Paris → Marseille.', FALSE),
(3, 'review_received', 'Nouvel avis', 'Jean Dupont vous a laissé un avis 5 étoiles!', TRUE),
(4, 'trip_reminder', 'Rappel de trajet', 'Votre trajet Marseille → Nice est prévu demain à 16h00.', FALSE),
(5, 'payment_pending', 'Paiement en attente', 'N\'oubliez pas de finaliser le paiement pour votre réservation.', FALSE),
(6, 'booking_confirmed', 'Réservation confirmée', 'Votre réservation pour Paris → Bordeaux le 12/12 a été confirmée.', TRUE);

-- ============================================
-- 插入举报记录
-- ============================================
INSERT INTO reports (reporter_id, reported_user_id, report_type, description, status) VALUES
(4, 3, 'inappropriate_behavior', 'Le conducteur était très impoli durant le trajet.', 'resolved'),
(1, NULL, 'technical_issue', 'Impossible de télécharger ma facture depuis l\'application.', 'pending'),
(2, 5, 'no_show', 'Le passager ne s\'est pas présenté au point de rendez-vous.', 'investigating');