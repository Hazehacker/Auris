--
-- PostgreSQL database dump
--
-- 1. 确保数据库存在（18 镜像里 postgres 超级用户有权限）
CREATE DATABASE auris;

-- 2. 连接到新库
\c auris;

\restrict Z3tex5x4kEB3O6gnkQ8COU4FKh5JAw9QyeLSwld6p4OEpBnDorP7SRHXAMmgQcz

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: lyrics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lyrics (
    id bigint NOT NULL,
    track_id bigint NOT NULL,
    language character varying(10),
    content text,
    create_time timestamp without time zone
);


ALTER TABLE public.lyrics OWNER TO postgres;

--
-- Name: COLUMN lyrics.track_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.lyrics.track_id IS '音乐id';


--
-- Name: COLUMN lyrics.language; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.lyrics.language IS '语言（''zh'' / ''en''）';


--
-- Name: COLUMN lyrics.content; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.lyrics.content IS 'LRC文本内容';


--
-- Name: COLUMN lyrics.create_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.lyrics.create_time IS '上传时间';


--
-- Name: lyrics_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lyrics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lyrics_id_seq OWNER TO postgres;

--
-- Name: lyrics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lyrics_id_seq OWNED BY public.lyrics.id;


--
-- Name: play_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.play_history (
    id bigint NOT NULL,
    track_id bigint,
    listened_time timestamp without time zone,
    user_id bigint
);


ALTER TABLE public.play_history OWNER TO postgres;

--
-- Name: COLUMN play_history.track_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.play_history.track_id IS '音乐id';


--
-- Name: COLUMN play_history.listened_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.play_history.listened_time IS '上一次播放的时间';


--
-- Name: COLUMN play_history.user_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.play_history.user_id IS '所属用户';


--
-- Name: play_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.play_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.play_history_id_seq OWNER TO postgres;

--
-- Name: play_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.play_history_id_seq OWNED BY public.play_history.id;


--
-- Name: playlist; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.playlist (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    sort smallint,
    slug character varying(255),
    status boolean NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone,
    user_id bigint NOT NULL
);


ALTER TABLE public.playlist OWNER TO postgres;

--
-- Name: playlist_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.playlist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.playlist_id_seq OWNER TO postgres;

--
-- Name: playlist_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.playlist_id_seq OWNED BY public.playlist.id;


--
-- Name: playlist_tracks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.playlist_tracks (
    id bigint NOT NULL,
    playlist_id bigint NOT NULL,
    track_id bigint NOT NULL,
    order_index integer
);


ALTER TABLE public.playlist_tracks OWNER TO postgres;

--
-- Name: COLUMN playlist_tracks.playlist_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.playlist_tracks.playlist_id IS '歌单id';


--
-- Name: COLUMN playlist_tracks.track_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.playlist_tracks.track_id IS '音乐id';


--
-- Name: COLUMN playlist_tracks.order_index; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.playlist_tracks.order_index IS '排序字段';


--
-- Name: playlist_tracks_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.playlist_tracks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.playlist_tracks_id_seq OWNER TO postgres;

--
-- Name: playlist_tracks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.playlist_tracks_id_seq OWNED BY public.playlist_tracks.id;


--
-- Name: track; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.track (
    id bigint NOT NULL,
    title character varying(255) NOT NULL,
    artist character varying(255),
    album character varying(255),
    duration integer,
    file_path character varying(2048),
    cover_url character varying(512),
    source_type character varying(10),
    create_time timestamp without time zone
);


ALTER TABLE public.track OWNER TO postgres;

--
-- Name: COLUMN track.title; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.title IS '歌曲标题';


--
-- Name: COLUMN track.artist; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.artist IS '歌手';


--
-- Name: COLUMN track.album; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.album IS '专辑';


--
-- Name: COLUMN track.duration; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.duration IS '时长(秒)';


--
-- Name: COLUMN track.file_path; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.file_path IS '本地文件路径或在线URL';


--
-- Name: COLUMN track.cover_url; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.cover_url IS '封面图片URL';


--
-- Name: COLUMN track.source_type; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.source_type IS '来源类型（local / online）';


--
-- Name: COLUMN track.create_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.track.create_time IS '创建时间';


--
-- Name: track_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.track_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.track_id_seq OWNER TO postgres;

--
-- Name: track_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.track_id_seq OWNED BY public.track.id;


--
-- Name: user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."user" (
    id bigint NOT NULL,
    username character varying(32),
    password character varying(255),
    phone character varying(11),
    email character varying(100),
    avatar character varying(1000),
    gender smallint,
    status boolean NOT NULL,
    role smallint NOT NULL,
    create_time timestamp without time zone,
    update_time timestamp without time zone
);


ALTER TABLE public."user" OWNER TO postgres;

--
-- Name: COLUMN "user".id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public."user".id IS '主键';


--
-- Name: COLUMN "user".avatar; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public."user".avatar IS '头像图片路径';


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_id_seq OWNER TO postgres;

--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_id_seq OWNED BY public."user".id;


--
-- Name: lyrics id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lyrics ALTER COLUMN id SET DEFAULT nextval('public.lyrics_id_seq'::regclass);


--
-- Name: play_history id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.play_history ALTER COLUMN id SET DEFAULT nextval('public.play_history_id_seq'::regclass);


--
-- Name: playlist id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.playlist ALTER COLUMN id SET DEFAULT nextval('public.playlist_id_seq'::regclass);


--
-- Name: playlist_tracks id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.playlist_tracks ALTER COLUMN id SET DEFAULT nextval('public.playlist_tracks_id_seq'::regclass);


--
-- Name: track id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.track ALTER COLUMN id SET DEFAULT nextval('public.track_id_seq'::regclass);


--
-- Name: user id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user" ALTER COLUMN id SET DEFAULT nextval('public.user_id_seq'::regclass);


--
-- Data for Name: lyrics; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lyrics (id, track_id, language, content, create_time) FROM stdin;
1	9	\N	[ti:晴天]\r\n[ar:周杰伦]\r\n[al:叶惠美]\r\n[by:HarmonyPlayer Test]\r\n[offset:0]\r\n\r\n[00:00.00]前奏\r\n[00:12.30]故事的小黄花\r\n[00:16.85]从出生那年就飘着\r\n[00:21.40]童年的荡秋千\r\n[00:25.90]随记忆一直晃到现在\r\n[00:30.50]\r\n[00:31.20]Re So So Si Do Si La\r\n[00:36.00]So La Si Si Si Si La Si La So\r\n[00:40.70]吹着前奏望着天空\r\n[00:45.25]我想起花瓣试着掉落\r\n[00:50.10]\r\n[00:51.00]为你翘课的那一天\r\n[00:55.60]花落的那一天\r\n[01:00.20]教室的那一间\r\n[01:04.80]我怎么看不见\r\n[01:09.40]消失的下雨天\r\n[01:14.00]我好想再淋一遍\r\n[01:18.60]没想到失去的勇气我还留着\r\n[01:23.90]\r\n[01:24.50]好想再问一遍\r\n[01:29.10]你会等待还是离开	2025-12-29 18:09:17.218178
\.


--
-- Data for Name: play_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.play_history (id, track_id, listened_time, user_id) FROM stdin;
\.


--
-- Data for Name: playlist; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.playlist (id, name, sort, slug, status, create_time, update_time, user_id) FROM stdin;
1	周杰伦系列1	3	zhoujielun	t	2025-12-28 23:35:10	2025-12-28 23:43:46.43713	1
2	周杰伦系列2	2	\N	t	2025-12-28 23:48:16.430455	2025-12-28 23:48:16.430455	1
3	周杰伦系列3	2	\N	t	2025-12-29 09:43:21.012863	2025-12-29 09:43:21.015522	2
4	周杰伦系列4	5	\N	t	2025-12-29 10:52:55.214912	2025-12-29 10:52:55.214912	2
10	歌单1	1	\N	t	2025-12-31 12:07:32.715961	2025-12-31 12:07:32.717954	3
6	周杰伦系列1	1	\N	t	2025-12-31 12:01:44.813956	2025-12-31 12:52:04.241601	3
5	纯音乐	1	\N	t	2025-12-31 12:01:22.379295	2025-12-31 13:00:43.675411	3
7	新建歌单 (2)	1	\N	f	2025-12-31 12:01:45.967953	2025-12-31 13:06:13.888158	3
8	新建歌单 (3)	1	\N	f	2025-12-31 12:01:46.595971	2025-12-31 13:06:16.620913	3
9	新建歌单 (4)	1	\N	f	2025-12-31 12:01:54.055074	2025-12-31 13:06:23.126998	3
\.


--
-- Data for Name: playlist_tracks; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.playlist_tracks (id, playlist_id, track_id, order_index) FROM stdin;
6	3	7	5
5	3	6	4
4	3	5	3
7	3	8	2
8	3	9	1
9	10	10	0
10	5	11	0
\.


--
-- Data for Name: track; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.track (id, title, artist, album, duration, file_path, cover_url, source_type, create_time) FROM stdin;
5	反方向的钟	周杰伦	专辑1	\N	\N	1234	\N	2025-12-29 16:48:35.046813
6	等你下课	周杰伦	专辑1	\N	\N	12345	\N	2025-12-29 16:48:54.077538
7	青花瓷	周杰伦	专辑1	\N	\N	12345	\N	2025-12-29 17:21:55.810871
8	以父之名	周杰伦	专辑1	\N	\N	12345	\N	2025-12-29 17:22:09.733952
9	晴天	周杰伦	专辑1	\N	\N	12345	\N	2025-12-29 17:22:33.523094
10	1	未知	\N	\N	https://auris-hazenix-top.oss-cn-heyuan.aliyuncs.com/6cb9a121-4bae-46f9-bbea-544a97f2520d.mp3	\N	\N	2025-12-31 12:10:37.989021
11	晴天	周杰伦	\N	\N	https://auris-hazenix-top.oss-cn-heyuan.aliyuncs.com/1d516c19-051e-48c9-9e2e-07fc4217ad7d.mp3	\N	\N	2025-12-31 12:16:31.26476
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."user" (id, username, password, phone, email, avatar, gender, status, role, create_time, update_time) FROM stdin;
2	Hazenix	202cb962ac59075b964b07152d234b70	\N	3542495585@qq.com	123	0	t	2	2025-12-26 22:29:42.647367	2025-12-26 22:49:33.8987
3	admin	21232f297a57a5a743894a0e4a801fc3	\N	3542495583@qq.com	\N	\N	t	2	2025-12-31 11:39:56.589586	2025-12-31 11:39:56.589586
\.


--
-- Name: lyrics_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lyrics_id_seq', 1, true);


--
-- Name: play_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.play_history_id_seq', 1, false);


--
-- Name: playlist_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.playlist_id_seq', 10, true);


--
-- Name: playlist_tracks_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.playlist_tracks_id_seq', 10, true);


--
-- Name: track_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.track_id_seq', 11, true);


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_id_seq', 3, true);


--
-- Name: lyrics lyrics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lyrics
    ADD CONSTRAINT lyrics_pkey PRIMARY KEY (id);


--
-- Name: play_history play_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.play_history
    ADD CONSTRAINT play_history_pkey PRIMARY KEY (id);


--
-- Name: playlist playlist_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.playlist
    ADD CONSTRAINT playlist_pkey PRIMARY KEY (id);


--
-- Name: playlist_tracks playlist_tracks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.playlist_tracks
    ADD CONSTRAINT playlist_tracks_pkey PRIMARY KEY (id);


--
-- Name: track track_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.track
    ADD CONSTRAINT track_pkey PRIMARY KEY (id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

\unrestrict Z3tex5x4kEB3O6gnkQ8COU4FKh5JAw9QyeLSwld6p4OEpBnDorP7SRHXAMmgQcz

