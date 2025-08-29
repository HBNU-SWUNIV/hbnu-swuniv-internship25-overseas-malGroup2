<script>
  import { user } from '$lib/stores/user';
  import { goto } from '$app/navigation';
  import { onMount } from 'svelte';
  import { get } from 'svelte/store';

  const API_BASE = 'http://localhost:8081';

  // 데이터
  let assets = [];
  let loadingAssets = false;
  let loadErr = '';

  // ✅ 검색어 입력값(타이핑 중인 값)
  let search = '';
  // ✅ 실제 적용된 검색어(아이콘 클릭/엔터 시에만 갱신)
  let appliedSearch = '';

  // ✅ 우측 패널에 표시할 선택 자산
  let selectedAsset = null;

  // ✅ 삭제 선택 상태
  let selectedIds = [];   // 체크된 asset_id 들
  let deleting = false;

  // ✅ 페이지네이션 상태
  const PAGE_SIZE = 6;
  const PAGE_WINDOW = 7;
  let currentPage = 1;

  // ▼ 통합 검색 필터
  const norm = (v) => (v ?? '').toString().toLowerCase();
  function assetMatches(a, term) {
    const t = norm(term);
    if (!t) return true;
    const bag = [
      a?.assetId, a?.assetType, a?.assetCategory, a?.brand, a?.model, a?.serialNumber,
      a?.purchaseDate, a?.supplier, a?.purchaseOrder, a?.warrantyStartDate, a?.warrantyEndDate,
      a?.campus, a?.locationRoom, a?.department, a?.custodianPerson, a?.status
    ].map(norm).join(' ');
    return bag.includes(t);
  }

  // ✅ 필터/페이지네이션은 "적용된 검색어" 기준
  $: filteredAssets = appliedSearch ? assets.filter(a => assetMatches(a, appliedSearch)) : assets;
  $: totalPages = Math.max(1, Math.ceil(filteredAssets.length / PAGE_SIZE));
  $: pageSliceStart = (currentPage - 1) * PAGE_SIZE;
  $: pageSliceEnd = pageSliceStart + PAGE_SIZE;
  $: paginatedAssets = filteredAssets.slice(pageSliceStart, pageSliceEnd);

  // ✅ 페이지 숫자 묶음 계산
  $: windowStart = Math.floor((currentPage - 1) / PAGE_WINDOW) * PAGE_WINDOW + 1;
  $: windowEnd = Math.min(windowStart + PAGE_WINDOW - 1, totalPages);
  $: windowPages = Array.from({ length: windowEnd - windowStart + 1 }, (_, i) => windowStart + i);

  onMount(() => {
    if (!get(user)) {
      goto('/login', { replaceState: true });
      return;
    }
    loadAssets();
  });

  // ✅ 로고 클릭 시 검색 초기화
  onMount(() => {
    const logo = document.querySelector('.logo');
    if (!logo) return;
    const onClick = (e) => {
      e.preventDefault?.();
      search = '';
      appliedSearch = '';
      currentPage = 1;
    };
    logo.addEventListener('click', onClick);
    return () => logo.removeEventListener('click', onClick);
  });

  async function loadAssets() {
    loadingAssets = true;
    loadErr = '';
    try {
      const res = await fetch(`${API_BASE}/api/assets`, { headers: { Accept: 'application/json' } });
      if (!res.ok) throw new Error(`GET /api/assets -> ${res.status}`);
      const data = await res.json();
      assets = Array.isArray(data) ? data : [];
      if (currentPage > Math.ceil(assets.length / PAGE_SIZE)) currentPage = 1;
    } catch (e) {
      console.error(e);
      loadErr = '자산 목록을 불러오지 못했습니다.';
    } finally {
      loadingAssets = false;
    }
  }

  function logout() {
    user.set(null);
    goto('/login', { replaceState: true });
  }

  const joinLocation = (a) => {
    const campus = (a?.campus ?? '').toString().trim();
    const room = (a?.locationRoom ?? '').toString().trim();
    return [campus, room].filter(Boolean).join(' ');
  };

  // ✅ 날짜 포맷(YYYY-MM-DD)
  const fmtDate = (d) => {
    if (!d) return '—';
    const s = String(d);
    return s.length >= 10 ? s.slice(0, 10) : s;
  };

  // ✅ 상세 보기
  function viewDetails(a) {
    selectedAsset = a;
  }

  // ✅ 선택 삭제 (기존 유지)
  async function deleteSelected() {
    if (selectedIds.length === 0 || deleting) return;
    if (!confirm(`선택한 ${selectedIds.length}개의 자산을 삭제하시겠습니까?`)) return;

    deleting = true;
    try {
      await Promise.all(
        selectedIds.map((id) =>
          fetch(`${API_BASE}/api/assets/${encodeURIComponent(id)}`, { method: 'DELETE' }).then((r) => {
            if (!r.ok && r.status !== 404) throw new Error(`DELETE ${id} -> ${r.status}`);
          })
        )
      );

      const delSet = new Set(selectedIds);
      assets = assets.filter((a) => !delSet.has(a.assetId));
      if (selectedAsset && delSet.has(selectedAsset.assetId)) selectedAsset = null;
      selectedIds = [];

      const maxPage = Math.max(1, Math.ceil(assets.length / PAGE_SIZE));
      if (currentPage > maxPage) currentPage = maxPage;
    } catch (e) {
      console.error(e);
      alert('삭제 중 오류가 발생했습니다.');
    } finally {
      deleting = false;
    }
  }

  // ✅ 페이지 이동
  function goToPage(n) {
    if (n < 1 || n > totalPages) return;
    currentPage = n;
  }
  function prevPage() { goToPage(currentPage - 1); }
  function nextPage() { goToPage(currentPage + 1); }

  // ✅ 페이지 창 이동
  function prevWindow() {
    if (windowStart > 1) goToPage(windowStart - 1);
  }
  function nextWindow() {
    if (windowEnd < totalPages) goToPage(windowEnd + 1);
  }

  // ✅ 검색 버튼/엔터 입력 시에만 검색 적용
  function applySearch() {
    appliedSearch = search;
    currentPage = 1;
  }
</script>

<svelte:head>
  <title>YELLOW SOCKS — Home</title>
</svelte:head>

<!-- 페이지 고유 콘텐츠 -->
<div class="main">
  <!-- 좌측 사이드바 -->
  <aside class="sidebar">
    <nav class="menu">
      <h3>Asset Menu</h3>
      <a class="menu-item active">- Management</a>
      <!-- ✅ 여기만 수정: Movement 클릭 시 /home/movement 이동 -->
      <a class="menu-item" href="/home/admin/movement">- Movement</a>

      <h3 class="mt">Notification</h3>
      <ul class="notif">
        <li class="pill">
          <button class="pill-x" aria-label="close">×</button>
          <b>• Product Name</b><span class="pill-sub">Laptop</span>
        </li>
        <li class="pill">
          <button class="pill-x" aria-label="close">×</button>
          <b>• Rental Date</b><span class="pill-sub">2025-08-20</span>
        </li>
        <li class="pill">
          <button class="pill-x" aria-label="close">×</button>
          <b>• Return Date</b><span class="pill-sub">2025-08-27</span>
        </li>
        <li class="pill">
          <button class="pill-x" aria-label="close">×</button>
          <b>• D-Day</b><span class="pill-sub">D-3 !!!</span>
        </li>
      </ul>
    </nav>

    <button class="logout-side" on:click={logout}>logout</button>
  </aside>

  <!-- 중앙 콘텐츠 -->
  <section class="content">
    <!-- 검색 바 -->
    <div class="search-wrap">
      <input
        class="search"
        placeholder="Enter the word you want to search for"
        bind:value={search}
        on:keydown={(e) => { if (e.key === 'Enter') { e.preventDefault(); applySearch(); } }}
      />
      <button class="search-btn" aria-label="search" on:click={applySearch}>🔍</button>
    </div>

    <!-- 카드 리스트 -->
    <div class="board">
      <ul class="card-list">
        {#if loadingAssets}
          <li class="card"><div class="meta">로딩 중…</div></li>
        {:else if loadErr}
          <li class="card"><div class="meta err">{loadErr}</div></li>
        {:else if filteredAssets.length === 0}
          <li class="card"><div class="meta">{appliedSearch ? '검색 결과가 없습니다.' : '자산이 없습니다.'}</div></li>
        {:else}
          {#each paginatedAssets as a}
            <li class="card">
              <div class="meta">
                <div class="row">
                  <span>Type : <b>{a?.assetType ?? '—'}</b></span>
                  <span>Department : <b>{a?.department ?? '—'}</b></span>
                </div>
                <div class="row">
                  <span>Model : <b>{a?.model ?? '—'}</b></span>
                  <span>Custodian : <b>{a?.custodianPerson ?? '—'}</b></span>
                </div>
                <div class="row">
                  <span>Location : <b>{joinLocation(a) || '—'}</b></span>
                  <span>Status : <b>{a?.status ?? '—'}</b></span>
                </div>
              </div>

              <div class="actions">
                <label class="chk">
                  <input type="checkbox" bind:group={selectedIds} value={a.assetId} />
                  <span></span>
                </label>

                <button class="edit" type="button">Edit</button>
                <button class="detail" type="button" on:click={() => viewDetails(a)}>View Details</button>
              </div>
            </li>
          {/each}
        {/if}
      </ul>

      <!-- ✅ 페이지네이션 -->
      <div class="pager">
        <button class="pg small" on:click={prevWindow} disabled={windowStart === 1}>◀</button>

        {#each windowPages as n}
          <button
            class="pg {currentPage === n ? 'active' : ''}"
            aria-current={currentPage === n ? 'page' : undefined}
            on:click={() => goToPage(n)}
          >
            {n}
          </button>
        {/each}

        <button class="pg small" on:click={nextWindow} disabled={windowEnd === totalPages}>▶</button>

        <button
          class="delete"
          on:click={deleteSelected}
          disabled={selectedIds.length === 0 || deleting}
          title={selectedIds.length ? `${selectedIds.length}개 선택됨` : '선택된 항목 없음'}
        >
          {deleting ? 'Deleting…' : `Delete${selectedIds.length ? ` (${selectedIds.length})` : ''}`}
        </button>
      </div>
    </div>
  </section>

  <!-- 우측 패널 -->
  <aside class="right-panel">
    <div class="panel-head">
      <ul class="dots"><li class="dot"></li><li class="dot"></li><li class="dot"></li></ul>
      <button class="panel-x" aria-label="close" on:click={() => (selectedAsset = null)}>✕</button>
    </div>

    <ul class="detail-list">
      <li><b>• Asset ID</b><div class="dim">{selectedAsset?.assetId ?? '—'}</div></li>
      <li><b>• Asset Type</b><div class="dim">{selectedAsset?.assetType ?? '—'}</div></li>
      <li><b>• Asset Category</b><div class="dim">{selectedAsset?.assetCategory ?? '—'}</div></li>
      <li><b>• Brand & Model</b>
        <div class="dim">
          {(selectedAsset?.brand ?? '—')}{selectedAsset?.model ? ` / ${selectedAsset.model}` : ''}
        </div>
      </li>
      <li><b>• Serial Number</b><div class="dim">{selectedAsset?.serialNumber ?? '—'}</div></li>
      <li><b>• Purchase Date</b><div class="dim">{fmtDate(selectedAsset?.purchaseDate)}</div></li>
      <li><b>• Supplier / Vendor</b><div class="dim">{selectedAsset?.supplier ?? '—'}</div></li>
      <li><b>• Purchase Order</b><div class="dim">{selectedAsset?.purchaseOrder ?? '—'}</div></li>
      <li><b>• Warranty Start Date</b><div class="dim">{fmtDate(selectedAsset?.warrantyStartDate)}</div></li>
      <li><b>• Warranty End Date</b><div class="dim">{fmtDate(selectedAsset?.warrantyEndDate)}</div></li>
      <li><b>• Location</b><div class="dim">{selectedAsset ? (joinLocation(selectedAsset) || '—') : '—'}</div></li>
      <li><b>• Department / Faculty assigned</b><div class="dim">{selectedAsset?.department ?? '—'}</div></li>
      <li><b>• Custodian / Responsible Person</b><div class="dim">{selectedAsset?.custodianPerson ?? '—'}</div></li>
      <li><b>• Status</b><div class="dim">{selectedAsset?.status ?? '—'}</div></li>
    </ul>

    <a class="edit-link">Edit Details</a>
  </aside>
</div>

<style>
  .main{
    display:grid;
    grid-template-columns: 280px minmax(0, 1fr) 420px;
    gap:22px;
    padding:16px 18px 22px;
    background:#e7d8b6;
    flex:1;
  }

  .sidebar{
    background:#fff; border:1px solid #eadfbe; border-radius:12px;
    padding:18px; display:flex; flex-direction:column; justify-content:space-between;
  }
  .menu h3{ margin:8px 0 10px; color:#6b5218; font-weight:800; font-size:20px; }
  .menu .mt{ margin-top:22px; }
  .menu-item{ display:block; color:#4a3b16; text-decoration:none; margin:6px 0; padding:2px 0; }
  .menu-item.active{ font-weight:800; }

  .notif{ list-style:none; padding:0; margin:10px 0 0; display:grid; gap:10px; }
  .pill{
    position:relative; background:#f7f1de; border:1px solid #eadfbe;
    border-radius:12px; padding:10px 12px 12px 14px; color:#6c5a20;
  }
  .pill-x{
    position:absolute; right:8px; top:6px; width:18px; height:18px; border:none;
    border-radius:999px; background:#f0e3b8; color:#7d6a1d; cursor:pointer;
    line-height:18px; font-size:12px;
  }
  .pill-sub{ display:block; margin-top:6px; padding:6px 10px; border-radius:10px; background:#fff; color:#8b8b8b; }

  .logout-side{
    align-self:flex-start; margin-top:20px;
    background:transparent; border:none; color:#8b7425; cursor:pointer;
  }

  .content{
    background:transparent; padding:0; border:none;
    display:flex; flex-direction:column; gap:14px;
  }

  .search-wrap{
    position:relative; margin:0 auto; width:min(780px, 100%);
    background:#f3ebd4; border:1px solid #e7dbbd; border-radius:30px; padding:12px 48px 12px 18px;
    box-shadow: inset 0 1px 0 rgba(255,255,255,.8);
  }
  .search{ width:100%; height:36px; border:none; outline:none; background:transparent; font-size:14px; color:#6e6e6e; }
  .search-btn{
    position:absolute; right:8px; top:8px; width:40px; height:40px; border:none;
    background:#fff; border-radius:999px; cursor:pointer; box-shadow:0 1px 2px rgba(0,0,0,.08);
  }

  .board{
    background:#e1d2ad8a; border:1px solid #decfa6; border-radius:20px; padding:18px;
    box-shadow: inset 0 1px 0 rgba(255,255,255,.6);
  }

  .card-list{ list-style:none; padding:0; margin:0; display:flex; flex-direction:column; gap:14px; }
  .card{
    display:grid; grid-template-columns: 1fr 120px; align-items:center; gap:14px;
    background:#fff; border:1px solid #e1d9c6; border-radius:8px; padding:10px 12px;
  }

  .meta{ color:#5c5141; font-size:13px; display:grid; gap:6px; }
  .meta b{ font-weight:800; color:#403520; }
  .meta.err{ color:#b91c1c; }
  .row{ display:flex; justify-content:space-between; gap:24px; }

  .actions{ display:grid; grid-template-rows:auto auto auto; gap:8px; justify-items:end; }

  .chk{ position:relative; width:18px; height:18px; }
  .chk input{ position:absolute; inset:0; opacity:0; cursor:pointer; }
  .chk span{
    display:block; width:18px; height:18px;
    border:1px solid #dacb9e; border-radius:4px; background:#fff;
    transition: background .15s ease, border-color .15s ease, box-shadow .15s ease;
  }
  .chk input:focus + span{ box-shadow:0 0 0 3px rgba(209,169,49,.25); }
  .chk input:checked + span{ background:#fff7e0; border-color:#d1a931; }
  .chk input:checked + span::after{
    content:'✓'; position:absolute; inset:0; display:grid; place-items:center;
    font-size:12px; font-weight:800; color:#6b5a20;
  }

  .edit{ border:1px solid #e0d6b8; background:#fff; color:#7b6a3f; border-radius:6px; padding:6px 10px; cursor:pointer; font-size:12px; }
  .detail{ border:1px solid #e0d6b8; background:#fff7e0; color:#6b5a20; border-radius:6px; padding:6px 10px; cursor:pointer; font-size:12px; }

  .pager{
    display:flex; align-items:center; gap:8px; justify-content:center; margin-top:14px; position:relative;
  }
  .pg{
    height:28px; min-width:28px; padding:0 8px; border:1px solid #ded7c7; background:#fff; border-radius:6px; cursor:pointer;
  }
  .pg.small{ min-width:24px; height:26px; }
  .pg[disabled]{ opacity:.45; cursor:not-allowed; }
  .pg.active{ background:#f3ebd4; border-color:#d6c89f; font-weight:700; }
  .delete{
    position:absolute; right:8px; bottom:-6px;
    padding:6px 12px; font-size:12px; border:1px solid #e7c6c6; color:#a14c4c; background:#ffe8e8; border-radius:6px; cursor:pointer;
  }

  .right-panel{
    background:#fff; border:1px solid #eadfbe; border-radius:18px; padding:14px 16px;
    align-self:start; max-height:66vh; min-height:600px; overflow:auto;
  }
  .panel-head{ display:flex; align-items:center; justify-content:space-between; }
  .dots{ display:flex; gap:6px; padding:0; margin:4px 0 10px; list-style:none; }
  .dot{ width:6px; height:6px; border-radius:999px; background:#d1a931; opacity:.75; }
  .panel-x{ border:none; background:#f7f1de; border:1px solid #eadfbe; width:28px; height:28px; border-radius:999px; cursor:pointer; color:#7a6521; }
  .detail-list{ list-style:none; padding:0 0 10px 0; margin:0; display:grid; gap:10px; color:#6b5a20; }
  .detail-list b{ color:#6b5a20; }
  .dim{ color:#3f3f3f; margin-top:4px; word-break:break-word; }
  .edit-link{ display:inline-block; margin-top:8px; color:#8a6a0e; cursor:pointer; text-decoration:underline; }

  @media (max-width: 1180px){
    .main{ grid-template-columns: 220px 1fr; }
    .right-panel{ display:none; }
  }
  @media (max-width: 760px){
    .main{ grid-template-columns: 1fr; }
    .sidebar{ order:-1; }
  }

  /* ✅ 로고 커서 표시 (전역 적용) */
  :global(.logo){ cursor:pointer; }
</style>
