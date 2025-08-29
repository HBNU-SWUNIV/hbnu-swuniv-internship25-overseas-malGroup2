<!-- src/routes/login/+page.svelte -->
<script>
  import { user } from '$lib/stores/user';
  import { goto } from '$app/navigation';

  let loginId = '';
  let password = '';
  let loading = false;
  let errorMsg = '';

  const USER_HOME = '/home/user';
  const ADMIN_HOME = '/home/admin';

  const API_BASE = 'http://localhost:8081';
  const normalize = (v) => String(v ?? '').trim();

  function swallowEnter(e) {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
    }
  }
  function noop() {}

  async function fetchById(customerId) {
    const res = await fetch(`${API_BASE}/api/members/${encodeURIComponent(customerId)}`, {
      method: 'GET',
      headers: { 'Accept': 'application/json' }
    });
    if (res.status === 404) return null;
    if (!res.ok) throw new Error(`GET /api/members/{id} -> ${res.status}`);
    return await res.json();
  }

  async function searchMember(customerId, customerName) {
    const qs = new URLSearchParams({ customerId, customerName }).toString();
    const res = await fetch(`${API_BASE}/api/members/search?${qs}`, {
      method: 'GET',
      headers: { 'Accept': 'application/json' }
    });
    if (!res.ok) throw new Error(`GET /api/members/search -> ${res.status}`);
    const list = await res.json();
    const id = normalize(customerId);
    const name = normalize(customerName);
    return (list || []).find(
      (m) => normalize(m.customerId) === id && normalize(m.customerName) === name
    ) ?? null;
  }

  async function loginAndRoute(expect) {
    errorMsg = '';
    if (!loginId || !password) {
      errorMsg = '아이디와 비밀번호를 모두 입력하세요.';
      return;
    }

    loading = true;
    try {
      let member = await fetchById(password);
      if (!member || normalize(member.customerName) !== normalize(loginId)) {
        member = await searchMember(password, loginId);
      }

      if (!member) {
        errorMsg = '아이디 또는 비밀번호가 올바르지 않습니다.';
        return;
      }

      const t = normalize(member.customerType).toLowerCase();
      if (expect === 'borrower' && t !== 'borrower') {
        errorMsg = 'User 계정이 아닙니다.';
        return;
      }
      if (expect === 'admin' && !(t === 'admin')) {
        errorMsg = '관리자(Lending/Inventory) 계정이 아닙니다.';
        return;
      }

      user.set({
        id: member.customerName,
        customerId: member.customerId,
        type: member.customerType
      });

      if (t === 'borrower') {
        await goto(USER_HOME);
      } else {
        await goto(ADMIN_HOME);
      }
    } catch (e) {
      console.error(e);
      errorMsg = '네트워크 또는 서버 오류가 발생했습니다.';
    } finally {
      loading = false;
    }
  }

  const onBorrowerLogin = () => loginAndRoute('borrower');
  const onAdminLogin = () => loginAndRoute('admin');
</script>

<svelte:head>
  <title>YELLOW SOCKS — Sign in</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
</svelte:head>

<main class="auth-wrap">
  <h1 class="brand">YELLOW SOCKS</h1>
  <h2 class="subtitle">Asset Management System</h2>

  <form
    class="card"
    on:submit|preventDefault={noop}
    on:keydown={swallowEnter}
    autocomplete="off"
  >
    <div class="field">
      <label for="login">Login</label>
      <input
        id="login"
        name="login"
        type="text"
        placeholder="Enter your ID"
        bind:value={loginId}
        required
      />
    </div>

    <div class="field">
      <label for="password">Password</label>
      <input
        id="password"
        name="password"
        type="password"
        placeholder="Enter your Password"
        bind:value={password}
        required
      />
    </div>

    {#if errorMsg}
      <p class="error" role="alert">{errorMsg}</p>
    {/if}

    <div class="btn-group">
      <button type="button" class="primary" on:click={onAdminLogin} disabled={loading}>
        {#if loading}Signing in…{/if}{#if !loading}Sign in as Admin{/if}
      </button>
      <button type="button" class="primary outline" on:click={onBorrowerLogin} disabled={loading}>
        {#if loading}Signing in…{/if}{#if !loading}Sign in as User{/if}
      </button>
    </div>

    <p class="meta">
      Don’t have an account?
      <a class="signup" href="/signup">Sign up now</a>
    </p>
  </form>
</main>

<style>
  :global(html), :global(body){
    background:#fff !important;
    margin:0;
    padding:0;
  }

  :root{
    --brand:#d1a931;
    --ink:#0f172a;
    --muted:#6b7280;
    --border:#e5e7eb;
    --ring:#c7d2fe;
    --card:#ffffff;
    --btn:#f6d03a;
    --btn-ink:#1f2937;
    --danger:#ef4444;
  }

  .auth-wrap{
    min-height: calc(100vh - 80px);
    display: grid;
    place-items: start center;
    padding-top: 8vh;
  }

  .brand{
    font-size: clamp(32px, 3.5vw, 42px);
    margin: 0 0 8px;
    font-weight: 900;
    letter-spacing: .8px;
    text-align: center;

    /* ✅ 더 진하고 선명한 골드 그라데이션 */
    background: linear-gradient(90deg, #FFD700, #FFC107, #B8860B);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    color: transparent;

    /* ✅ 입체감 강조 */
    text-shadow: 1px 1px 2px rgba(0,0,0,0.15);
  }

  .subtitle{
    font-size: clamp(24px, 2.8vw, 32px);
    margin: 0 0 22px;
    font-weight: 400;
    color: #9ca3af;
    text-align: center;
  }

  .card{
    width: min(420px, 92vw);
    background: var(--card);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 22px;
    box-shadow: 0 14px 40px rgba(16, 30, 55, .10);
  }

  .field{ margin-bottom: 16px; }
  label{
    display:block;
    font-size: 12px;
    color: var(--muted);
    margin: 0 0 6px;
  }

  input{
    width:100%;
    height:42px;
    border-radius: 10px;
    border:1px solid var(--border);
    padding: 0 12px;
    font-size: 14px;
    color: var(--ink);
    background:#fafafa;
    outline: none;
    transition: box-shadow .15s ease, border-color .15s ease, background .15s ease;
  }
  input::placeholder{ color:#9ca3af; }
  input:focus{
    background:#fff;
    border-color:#cbd5e1;
    box-shadow: 0 0 0 3px var(--ring);
  }

  .error{
    margin: 6px 0 0 0;
    color: var(--danger);
    font-size: 13px;
  }

  .primary{
    width:100%;
    height:44px;
    margin-top: 10px;
    border: 1px solid #eab30833;
    background: var(--btn);
    color: var(--btn-ink);
    border-radius: 10px;
    font-weight: 600;
    cursor: pointer;
    transition: transform .05s ease, box-shadow .15s ease, filter .15s ease, opacity .15s ease;
    box-shadow: 0 6px 18px rgba(246, 208, 58, .35);
  }
  .primary.outline{
    background:#fff;
    border-color: var(--btn);
    box-shadow: none;
  }
  .primary[disabled]{ opacity:.7; cursor:not-allowed; }
  .primary:hover{ filter: brightness(1.02); }
  .primary.outline:hover{ background:#fffbe6; }
  .primary:active{ transform: translateY(1px); }

  .btn-group{
    display:grid;
    grid-template-columns: 1fr;
    gap: 10px;
    margin-top: 6px;
  }

  .meta{
    text-align:center;
    margin: 14px 0 0;
    font-size: 12px;
    color:#6b7280;
  }
  .signup{
    color:#0a66ff; text-decoration:none; margin-left:6px; font-weight:600;
  }
  .signup:hover{ text-decoration: underline; }

  @media (max-width: 560px){
    .auth-wrap{ padding: 28px 16px 18vh; }
    .card{ padding: 18px; }
  }
</style>
