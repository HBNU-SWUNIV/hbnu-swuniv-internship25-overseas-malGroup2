<script>
  import { user } from '$lib/stores/user';
  import { goto } from '$app/navigation';

  function logout() {
    user.set(null);
    goto('/login', { replaceState: true });
  }
</script>

<!-- 남길 상단바 -->
<header class="topbar">
  <h1 class="logo">YELLOW SOCKS</h1>
  <div class="spacer"></div>
  <div class="user-box">
    <span class="username">{$user?.id ?? 'user name'}</span>
    <span class="avatar" aria-hidden="true">👤</span>
    <button class="logout-top" on:click={logout}>logout</button>
  </div>
</header>

<slot />

<style>
  :global(html), :global(body){
    margin:0;
    font-family:system-ui, -apple-system, Segoe UI, Roboto, sans-serif;
    background:#efe9da;
  }

  /* 혹시 남아있다면 중앙 큰 타이틀 바(utilitybar) 숨김 */
  :global(.utilitybar){ display:none !important; }

  /* ✅ 추가: 첫 번째 .topbar만 보이고, 그 아래 등장하는 .topbar는 전부 숨김 */
  :global(.topbar ~ .topbar){ display:none !important; }

  .topbar{
    display:grid;
    grid-template-columns: auto 1fr auto;
    align-items:center;
    padding:16px 20px;
    background:#fff;
    border-bottom:1px solid #e6dfcf;
  }
  .logo{
    margin:0; font-size:28px; font-weight:900; letter-spacing:.4px;
    color:#e1b81f;
  }
  .spacer{ min-width:12px; }
  .user-box{ display:flex; align-items:center; gap:10px; color:#8b7425; }
  .username{ font-weight:700; }
  .avatar{ font-size:18px; }
  .logout-top{
    border:none; background:transparent; color:#8b7425; cursor:pointer; font-weight:700;
  }
</style>
